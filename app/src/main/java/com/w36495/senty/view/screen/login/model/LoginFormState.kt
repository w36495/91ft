package com.w36495.senty.view.screen.login.model

data class LoginFormState(
    val email: String = "",
    val password: String = "",
    val checkAutoLogin: Boolean = false,
) {
    fun updateEmail(newEmail: String): LoginFormState {
        return this.copy(email = newEmail)
    }

    fun updatePassword(newPassword: String): LoginFormState {
        return this.copy(password = newPassword)
    }

    fun updateAuthLoginState(state: Boolean): LoginFormState {
        return this.copy(checkAutoLogin = state)
    }
}