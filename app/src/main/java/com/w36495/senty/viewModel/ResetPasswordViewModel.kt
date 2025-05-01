package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.w36495.senty.util.validator.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private var _hasEmailError = MutableStateFlow(false)
    val hasEmailError = _hasEmailError.asStateFlow()
    private var _emailErrorMsg = MutableStateFlow("")
    val emailErrorMsg = _emailErrorMsg.asStateFlow()
    private var _result = MutableStateFlow(false)
    val result = _result.asStateFlow()

    private val _errorChannel = Channel<Throwable?>()
    val errorFlow = _errorChannel.receiveAsFlow()

    fun sendPasswordResetEmail(email: String) {
        if (!validateEmail(email)) return

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _result.value = true
                } else {
                    Log.d("ResetPasswordVM", task.exception?.stackTraceToString() ?: "error is null")
                    when (task.exception) {
                        is FirebaseAuthInvalidUserException -> setErrorMsg("존재하지 않는 이메일입니다.", _emailErrorMsg, _hasEmailError)
                        else -> {
                            viewModelScope.launch {
                                _errorChannel.send(task.exception)
                            }
                        }
                    }
                }
            }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            setErrorMsg("이메일을 입력해주세요.", _emailErrorMsg, _hasEmailError)
            return false
        } else if (!Validator.isValidEmail(email)) {
            setErrorMsg("이메일 형식으로 입력해주세요.", _emailErrorMsg, _hasEmailError)
            return false
        } else {
            setErrorMsg("", _emailErrorMsg, _hasEmailError)
        }

        return true
    }

    private fun setErrorMsg(
        msg: String,
        errorMsg: MutableStateFlow<String>,
        hasError: MutableStateFlow<Boolean>,
    ) {
        viewModelScope.launch {
            errorMsg.update { msg }
            if (msg == "") hasError.update { false }
            else hasError.update { true }
        }
    }
}