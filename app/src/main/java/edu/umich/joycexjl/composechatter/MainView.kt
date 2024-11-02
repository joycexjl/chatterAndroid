package edu.umich.joycexjl.composechatter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import edu.umich.joycexjl.composechatter.ChattStore.chatts
import edu.umich.joycexjl.composechatter.ChattStore.getChatts
import edu.umich.joycexjl.composechatter.ui.theme.Canary
import edu.umich.joycexjl.composechatter.ui.theme.Moss
import edu.umich.joycexjl.composechatter.ui.theme.WhiteSmoke
import java.util.Timer
import kotlin.concurrent.schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView() {
    val navController = LocalNavHostController.current
    var isRefreshing by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(R.string.chatter),
                    fontSize = 20.sp
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Canary,
                contentColor = Moss,
                shape = CircleShape,
                modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 10.dp).scale(1.2f).zIndex(1f),
                onClick = {
                    // navigate to PostView
                    navController.navigate("PostView")
                }
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.post), modifier = Modifier.scale(1.3f))
            }
        }
    ) {
        // content of Scaffold
        PullToRefreshBox(
            modifier = Modifier.fillMaxSize().zIndex(1f),
            isRefreshing = isRefreshing,    // hide or show loading icon
            onRefresh = {
                isRefreshing = true         // show loading icon
                getChatts()
                Timer().schedule(250) {     // let isRefreshing = true "settle"
                    isRefreshing = false    // hide loading icon
                }
            },
        ) {
            // describe the View
            val listState = rememberLazyListState()
            LaunchedEffect(chatts.size) {
                listState.animateScrollToItem(0)
            }

            LazyColumn(
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.padding(
                    it.calculateStartPadding(LayoutDirection.Ltr),
                    it.calculateTopPadding() + 10.dp,
                    it.calculateEndPadding(LayoutDirection.Ltr),
                    it.calculateBottomPadding()
                )
                    .background(color = WhiteSmoke),
                state = listState,
            ) {
                items(items = chatts, key = { it.id as Any }) {
                    ChattListRow(it)
                }
            }
        }

    }
}