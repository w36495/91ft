package com.w36495.senty.view.screen.signup.model

sealed interface SignUpEffect {
    data class ShowError(val message: String) : SignUpEffect
}