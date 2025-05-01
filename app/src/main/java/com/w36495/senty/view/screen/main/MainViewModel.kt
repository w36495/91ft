package com.w36495.senty.view.screen.main

import androidx.lifecycle.ViewModel
import com.w36495.senty.domain.error.ErrorMessageProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val errorMessageProvider: ErrorMessageProvider,
) : ViewModel() {
    fun getErrorMessageRes(throwable: Throwable?): Int {
        return errorMessageProvider.getMessageRes(throwable)
    }
}