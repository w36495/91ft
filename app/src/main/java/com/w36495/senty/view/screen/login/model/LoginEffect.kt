package com.w36495.senty.view.screen.login.model

sealed interface LoginEffect {
    data class ShowError(val message: String) : LoginEffect
    data object NavigateToHome : LoginEffect
}