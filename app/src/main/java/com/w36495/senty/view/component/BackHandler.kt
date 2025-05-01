package com.w36495.senty.view.component

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.w36495.senty.R
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog

@Composable
fun SentyBackHandler() {
    val context = LocalContext.current
    var backHandlerTime = System.currentTimeMillis()
    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler {
        if (System.currentTimeMillis() - backHandlerTime <= 400L) {
            showExitDialog = true
        }

        backHandlerTime = System.currentTimeMillis()
    }

    if (showExitDialog) {
        BasicAlertDialog(
            title = stringResource(id = R.string.common_exit_title),
            hasCancel = true,
            onDismiss = { showExitDialog = false },
            onComplete = { (context as Activity).finish() },
        )
    }
}
