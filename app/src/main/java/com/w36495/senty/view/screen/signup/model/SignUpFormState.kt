package com.w36495.senty.view.screen.signup.model

data class SignUpFormState(
    val email: String = "",
    val password: String = "",
    val passwordConfirm: String = "",
) {
    fun updateEmail(newEmail: String): SignUpFormState {
        return this.copy(email = newEmail)
    }

    fun updatePassword(newPassword: String): SignUpFormState {
        return this.copy(password = newPassword)
    }

    fun updatePasswordConfirm(newPasswordConfirm: String): SignUpFormState {
        return this.copy(passwordConfirm = newPasswordConfirm)
    }
}