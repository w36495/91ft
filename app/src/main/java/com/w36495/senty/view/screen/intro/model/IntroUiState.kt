package com.w36495.senty.view.screen.intro.model

sealed interface IntroUiState {
    data object Loading : IntroUiState
    data object LoggedIn : IntroUiState
    data object NotLoggedIn : IntroUiState
}