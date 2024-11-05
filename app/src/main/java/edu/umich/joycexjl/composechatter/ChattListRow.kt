package edu.umich.joycexjl.composechatter
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.umich.joycexjl.composechatter.ui.theme.Gray88
import edu.umich.joycexjl.composechatter.ui.theme.HeavenWhite
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import kotlin.math.abs
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChattListRow(chatt: Chatt) {
    var offsetX by remember { mutableStateOf(0f) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val deleteButtonWidth = 120.dp
    val draggableState = rememberDraggableState { delta: Float ->
        val newOffset = offsetX + delta
        offsetX = newOffset.coerceIn(-deleteButtonWidth.value, 0f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
    ) {
        // delete button
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(deleteButtonWidth),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.align(Alignment.CenterEnd))
            {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,

                )
            }
        }


        Column(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .fillMaxWidth()
                .background(color = if (chatt.altRow) Gray88 else HeavenWhite)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = draggableState,
                    onDragStopped = {
                        offsetX = if (abs(offsetX) > deleteButtonWidth.value / 2) {
                            -deleteButtonWidth.value
                        } else {
                            0f
                        }
                    }
                )
                .padding(8.dp, 0.dp, 8.dp, 0.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                chatt.username?.let {
                    Text(
                        it,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp)
                    )
                }

                chatt.timestamp?.let {
                    Text(
                        formatTimestamp(it),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.padding(4.dp, 8.dp, 4.dp, 0.dp)
                    )
                }
            }

            chatt.message?.let {
                Text(
                    it,
                    fontSize = 17.sp,
                    modifier = Modifier.padding(4.dp, 10.dp, 4.dp, 10.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                offsetX = 0f
            },
            title = { Text("Delete Message") },
            text = { Text("Are you sure you want to delete this message?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        ChattStore.deleteChatt(chatt) {
                            ChattStore.getChatts()
                        }
                        showDeleteDialog = false
                        offsetX = 0f
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        offsetX = 0f
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}

private fun formatTimestamp(timestamp: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH)
        // 确保时间戳格式正确
        val formattedTimestamp = if (!timestamp.contains("T")) {
            timestamp.replace(" ", "T")
        } else {
            timestamp
        }
        val date = sdf.parse(formattedTimestamp)
        val now = System.currentTimeMillis()
        val diff = now - date.time

        when {
            diff < 60_000 -> "just now"
            diff < 3600_000 -> {
                val mins = diff / 60_000
                "${mins} ${if (mins == 1L) "minute" else "minutes"} ago"
            }
            diff < 86400_000 -> {
                val hours = diff / 3600_000
                "${hours} ${if (hours == 1L) "hour" else "hours"} ago"
            }
            diff < 2592000_000 -> {
                val days = diff / 86400_000
                "${days} ${if (days == 1L) "day" else "days"} ago"
            }
            else -> SimpleDateFormat("MMM d", Locale.ENGLISH).format(date)
        }
    } catch (e: Exception) {
        Log.e("ChattList", e.toString())
        timestamp
    }
}