package com.w36495.senty.domain.error

interface ErrorMessageProvider {
    fun getMessageRes(throwable: Throwable?): Int
}