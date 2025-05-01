package com.w36495.senty.util.validator

object Validator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    private val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")

    fun isValidEmail(email: String): Boolean = emailRegex.matches(email)
    fun isValidPassword(password: String): Boolean = passwordRegex.matches(password)
}