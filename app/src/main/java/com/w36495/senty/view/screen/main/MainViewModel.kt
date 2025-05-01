package com.w36495.senty.view.screen.main

import androidx.lifecycle.ViewModel
import com.w36495.senty.R
import com.w36495.senty.data.error.AuthError
import com.w36495.senty.data.error.ErrorHandler
import com.w36495.senty.data.error.GlobalError
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val errorHandler: ErrorHandler,
) : ViewModel() {
    fun getErrorMessage(throwable: Throwable?): Int {
        val errorType = errorHandler.handleError(throwable)
        return when (errorType) {
            AuthError.ExistedEmail -> R.string.error_signup_exsist_email
            AuthError.FailedLogin -> R.string.error_login_failed
            AuthError.TooManyRequest -> R.string.error_login_failed_too_many_request
            GlobalError.NetWork -> R.string.common_error_message_network
            GlobalError.UnKnownError -> R.string.common_error_unknown
        }
    }
}