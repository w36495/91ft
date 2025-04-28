package com.w36495.senty.view.screen.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.view.screen.intro.model.IntroUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<IntroUiState>(IntroUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.checkLoginState()
                .onSuccess { isLoggedIn ->
                    _uiState.update {
                        if (isLoggedIn) IntroUiState.LoggedIn else IntroUiState.NotLoggedIn
                    }
                }
                .onFailure {
                    _uiState.update {
                        IntroUiState.NotLoggedIn
                    }
                }
        }
    }
}