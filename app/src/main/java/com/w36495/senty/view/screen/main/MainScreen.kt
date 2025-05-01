package com.w36495.senty.view.screen.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.screen.main.component.MainBottomTabBar
import com.w36495.senty.view.screen.main.navigation.MainNavHost
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    vm: MainViewModel = hiltViewModel(),
    navigator: MainNavigator = rememberMainNavigator(),
) {
    val coroutineScope = rememberCoroutineScope()
    val localContextResource = LocalContext.current.resources

    val snackBarHost = remember { SnackbarHostState() }

    val onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit = { throwable ->
        coroutineScope.launch {
            Log.d("GlobalError", throwable?.stackTraceToString() ?: "Error is null")
            val message = vm.getErrorMessage(throwable)
            snackBarHost.showSnackbar(localContextResource.getString(message))
        }
    }

    Scaffold(
        bottomBar = {
            MainBottomTabBar(
                isVisible = navigator.showBottomBar(),
                tabs = MainBottomTab.entries.toList(),
                currentTab = navigator.currentTab,
                onTabSelected = { navigator.navigate(it) },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHost) },
        modifier = Modifier.navigationBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            MainNavHost(
                navigator = navigator,
                padding = innerPadding,
                onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
            )
        }
    }
}