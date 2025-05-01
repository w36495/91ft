package com.w36495.senty.view.screen.intro

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.domain.repository.UserRepository
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
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<IntroUiState>(IntroUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.checkLoginState()
                .onSuccess { user ->
                    user?.let {
                        userRepository.updateUser(user)

                        Log.d("IntroVM", "🟢 자동 로그인 성공 : $user")
                        _uiState.update { IntroUiState.LoggedIn }
                    } ?: _uiState.update { IntroUiState.NotLoggedIn }
                }
                .onFailure {
                    Log.d("IntroVM", "🔴 자동 로그인 실패")
                    _uiState.update { IntroUiState.NotLoggedIn }
                }
        }
    }
}