package edu.umich.joycexjl.composechatter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.umich.joycexjl.composechatter.ChattStore.getChatts
import edu.umich.joycexjl.composechatter.ChattStore.postChatt
import edu.umich.joycexjl.composechatter.R.string.back
import edu.umich.joycexjl.composechatter.R.string.message
import edu.umich.joycexjl.composechatter.R.string.post
import edu.umich.joycexjl.composechatter.R.string.send
import edu.umich.joycexjl.composechatter.R.string.username

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostView() {
    val context = LocalContext.current
    val navController = LocalNavHostController.current
    val username = stringResource(R.string.username)
    var message by rememberSaveable { mutableStateOf(context.getString(R.string.message)) }

    @Composable
    fun ArrowBack() {
        IconButton(onClick = { navController.popBackStack() } ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(back))
        }
    }

    @Composable
    fun SubmitButton() {
        var canSend by rememberSaveable { mutableStateOf(true) }

        IconButton(onClick = {
            canSend = false
            postChatt(Chatt(username, message)) {
                getChatts()
            }
            navController.popBackStack()
        }, enabled = canSend) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                stringResource(R.string.send)
            )
        }
    }

    Scaffold(
        // put the topBar here
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.post),
                fontSize=20.sp) },
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                navigationIcon = { ArrowBack() },
                actions = { SubmitButton() }
            ) },
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.padding(
                it.calculateStartPadding(LayoutDirection.Ltr)+8.dp,
                it.calculateTopPadding(),
                it.calculateEndPadding(LayoutDirection.Ltr)+8.dp,
                it.calculateBottomPadding())
        ) {
            Text(username,
                Modifier
                    .padding(0.dp, 30.dp, 0.dp, 0.dp)
                    .fillMaxWidth(1f), textAlign= TextAlign.Center, fontSize = 20.sp
            )

            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .padding(8.dp, 20.dp, 8.dp, 0.dp)
                    .fillMaxWidth(.8f),
                textStyle = TextStyle(fontSize = 17.sp),
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent)
            )
        }
    }
}