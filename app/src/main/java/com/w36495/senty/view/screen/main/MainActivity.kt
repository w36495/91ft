package com.w36495.senty.view.screen.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.w36495.senty.domain.manager.VersionInfoManager
import com.w36495.senty.view.component.SentyBackHandler
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var versionInfoManager: VersionInfoManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        versionInfoManager.checkUpdate()

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