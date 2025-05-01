package com.w36495.senty.domain.error

sealed interface SentyError
sealed interface GlobalError : SentyError {
    data object UnKnownError : GlobalError
    data object NetWork : GlobalError
}

sealed interface AuthError : SentyError {
    data object FailedLogin : AuthError
    data object TooManyRequest : AuthError
    data object ExistedEmail : AuthError
}