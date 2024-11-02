package edu.umich.joycexjl.composechatter

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID
import kotlin.reflect.full.declaredMemberProperties

object ChattStore {
    private var isRetrieving = false

    private val _chatts = mutableStateListOf<Chatt>()
    val chatts: List<Chatt> = _chatts
    private val nFields = Chatt::class.declaredMemberProperties.size - 1

    private lateinit var queue: RequestQueue
//    private const val serverUrl = "https://mada.eecs.umich.edu/"
    private const val serverUrl = "https://qys.pipzza.pw/"

    fun initQueue(context: Context) {
        queue = newRequestQueue(context)
    }

    fun getChatts() {
        // only one outstanding retrieval
        synchronized (this) {
            if (isRetrieving) {
                return
            }
            isRetrieving = true
        }

        val getRequest = JsonObjectRequest("${serverUrl}getchatts/",
            { response ->
                val chattsReceived = try { response.getJSONArray("chatts") } catch (e: JSONException) { JSONArray() }
                var idx = 0

                _chatts.clear()
                for (i in 0 until chattsReceived.length()) {
                    val chattEntry = chattsReceived[i] as JSONArray
                    if (chattEntry.length() == nFields) {
                        _chatts.add(Chatt(username = chattEntry[0].toString(),
                            message = chattEntry[1].toString(),
                            id = UUID.fromString(chattEntry[2].toString()),
                            timestamp = chattEntry[3].toString(),
                            altRow = idx % 2 == 0))
                        idx += 1
                    } else {
                        Log.e("getChatts", "Received unexpected number of fields: " + chattEntry.length().toString() + " instead of " + nFields.toString())
                    }
                }
                synchronized(this) {
                    isRetrieving = false
                }
            },
            { e ->
                synchronized(this) {
                    isRetrieving = false
                }
                Log.e("getChatts", e.localizedMessage ?: "NETWORKING ERROR")
            }
        )

        queue.add(getRequest)
    }

    fun postChatt(chatt: Chatt, completion: () -> Unit) {
        val jsonObj = mapOf(
            "username" to chatt.username,
            "message" to chatt.message,
        )
        val postRequest = JsonObjectRequest(Request.Method.POST,
            "${serverUrl}postchatt/", JSONObject(jsonObj),
            { completion() },
            { e -> Log.e("postChatt", e.localizedMessage ?: "JsonObjectRequest error") }
        )

        queue.add(postRequest)
    }
}
