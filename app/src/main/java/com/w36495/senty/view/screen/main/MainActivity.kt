package com.w36495.senty.view.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.w36495.senty.view.component.SentyBackHandler
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val navigator = rememberMainNavigator()

            SentyTheme {
                MainScreen(
                    navigator = navigator,
                )

                SentyBackHandler()
            }
        }
    }
}