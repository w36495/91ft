package com.w36495.senty.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.w36495.senty.view.screen.AppScreen
import com.w36495.senty.view.screen.ui.theme.SentyTheme

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SentyTheme {
                AppScreen()
            }
        }
    }
}