package com.w36495.senty.util

import android.util.Patterns
import java.util.regex.Pattern

class StringUtils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun isValidPassword(password: String): Boolean {
            val passwordPattern = "^((?=.*[a-z])(?=.*[0-9]).{8,})$"
            return Pattern.matches(passwordPattern, password)
        }
    }
}