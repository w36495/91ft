package com.w36495.senty.view.screen.login.model

sealed interface LoginEffect {
    data class ShowError(val throwable: Throwable? = null) : LoginEffect
    data object NavigateToHome : LoginEffect
}