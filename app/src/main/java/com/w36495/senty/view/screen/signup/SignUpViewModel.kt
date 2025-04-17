package com.w36495.senty.view.screen.signup

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.w36495.senty.R
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.util.validator.Validator
import com.w36495.senty.view.screen.signup.model.SignUpEffect
import com.w36495.senty.view.screen.signup.model.SignUpFormState
import com.w36495.senty.view.screen.signup.model.SignUpUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _formState = MutableStateFlow(SignUpFormState())
    val formState = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState get() = _uiState.asStateFlow()

    private val _effect = Channel<SignUpEffect>()
    val effect = _effect.receiveAsFlow()

    val isEmailValid: StateFlow<Boolean> = formState
        .map { Validator.isValidEmail(it.email) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val isPasswordValid: StateFlow<Boolean> = formState
        .map { Validator.isValidPassword(it.password) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val isPasswordMatch: StateFlow<Boolean> = formState
        .map { it.password == it.passwordConfirm }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val isFormValid: StateFlow<Boolean> = combine(
        isEmailValid,
        isPasswordValid,
        isPasswordMatch
    ) { emailValid, passwordValid, passwordMatch ->
        emailValid && passwordValid && passwordMatch
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun createAccountWithEmail(context: Context) {
        viewModelScope.launch {
            _uiState.update { SignUpUiState.Loading }

            val result = authRepository.signUpWithEmail(formState.value.email, formState.value.password)

            result
                .onSuccess {
                    _uiState.update { SignUpUiState.Success }
                }
                .onFailure { throwable ->
                    Log.d("SignUpVM", throwable.stackTraceToString())

                    when (throwable) {
                        is FirebaseAuthUserCollisionException -> {
                            _effect.send(SignUpEffect.ShowError(context.getString(R.string.signup_error_check_email)))
                        }
                        else -> {
                            _effect.send(SignUpEffect.ShowError(context.getString(R.string.common_error1)))
                        }
                    }
                }
        }
    }

    fun updateEmail(newEmail: String) {
        _formState.update { it.updateEmail(newEmail) }
    }

    fun updatePassword(newPassword: String) {
        _formState.update { it.updatePassword(newPassword) }
    }

    fun updatePasswordConfirm(newPasswordConfirm: String) {
        _formState.update { it.updatePasswordConfirm(newPasswordConfirm) }
    }
}