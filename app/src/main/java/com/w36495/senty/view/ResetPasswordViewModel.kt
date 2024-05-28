package com.w36495.senty.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun sendPasswordResetEmail(email: String) {
        if (!validateEmail(email)) return

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _result.value = true
                }
            }
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            setErrorMsg("이메일을 입력해주세요.", _emailErrorMsg, _hasEmailError)
            return false
        } else if (!StringUtils.isValidEmail(email)) {
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