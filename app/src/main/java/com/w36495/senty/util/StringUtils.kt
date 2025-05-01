package com.w36495.senty.util

import android.util.Patterns
import java.util.Locale

class StringUtils {
    companion object {
        fun isValidEmail(email: String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun format2Digits(value: Int): String = String.format(Locale.KOREA, "%02d", value)
    }
}