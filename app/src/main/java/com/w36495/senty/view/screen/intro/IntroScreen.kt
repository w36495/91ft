package com.w36495.senty.view.screen.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.intro.model.IntroUiState
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.antonFontFamily
import kotlinx.coroutines.delay

@Composable
fun IntroRoute(
    vm: IntroViewModel = hiltViewModel(),
    moveToLogin: () -> Unit,
    moveToHome: () -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is IntroUiState.LoggedIn || uiState is IntroUiState.NotLoggedIn) {
            delay(1_500)

            when (uiState) {
                is IntroUiState.LoggedIn -> moveToHome()
                is IntroUiState.NotLoggedIn -> moveToLogin()
                else -> {}
            }
        }
    }

    IntroScreen()
}

@Preview(showBackground = true)
@Composable
private fun IntroScreen(

) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SentyGreen60),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.app_name).uppercase(),
            fontFamily = antonFontFamily,
            fontSize = 48.sp,
            color = SentyWhite,
        )
    }
}