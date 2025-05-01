package com.w36495.senty.data.error

import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.net.UnknownHostException
import javax.inject.Inject

interface ErrorHandler {
    fun handleError(throwable: Throwable?): SentyError
}

class ErrorHandlerImpl @Inject constructor(

) : ErrorHandler {
    override fun handleError(throwable: Throwable?): SentyError {
        return when (throwable) {
            is FirebaseAuthInvalidUserException,
            is FirebaseAuthInvalidCredentialsException -> AuthError.FailedLogin
            is FirebaseAuthUserCollisionException -> AuthError.ExistedEmail
            is FirebaseNetworkException -> GlobalError.NetWork
            is FirebaseAuthException -> {
                when (throwable.errorCode) {
                    "ERROR_TOO_MANY_REQUESTS" -> AuthError.TooManyRequest
                    "ERROR_NETWORK_REQUEST_FAILED" -> GlobalError.NetWork
                    else -> GlobalError.UnKnownError
                }
            }
            is ApiException -> {
                // 구글 로그인 관련 오류
                when (throwable.statusCode) {
                    7 -> GlobalError.NetWork
                    else -> GlobalError.UnKnownError
                }
            }
            is UnknownHostException -> GlobalError.NetWork
            else -> GlobalError.UnKnownError
        }
    }

}