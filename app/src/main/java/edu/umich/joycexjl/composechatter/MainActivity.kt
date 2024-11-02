package edu.umich.joycexjl.composechatter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.umich.joycexjl.composechatter.ChattStore.getChatts
import edu.umich.joycexjl.composechatter.ChattStore.initQueue
import edu.umich.joycexjl.composechatter.ui.theme.ComposeChatterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initQueue(applicationContext)
        getChatts()

        setContent {
            val navController = rememberNavController()
            CompositionLocalProvider(LocalNavHostController provides navController) {
                NavHost(navController, startDestination = "MainView") {
                    composable("MainView") {
                        MainView()
                    }
                    composable("PostView") {
                        PostView()
                    }
                }
            }
        }
    }
}

val LocalNavHostController = staticCompositionLocalOf<NavHostController> { error("LocalNavHostController provides no current")}
