package com.w36495.senty.domain.error

interface ErrorHandler {
    fun handleError(throwable: Throwable?): SentyError
}
