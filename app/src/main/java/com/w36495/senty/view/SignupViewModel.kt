package com.w36495.senty.view

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.w36495.senty.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private var _hasEmailError = MutableStateFlow(false)
    val hasEmailError = _hasEmailError.asStateFlow()
    private var _emailErrorMsg = MutableStateFlow("")
    val emailErrorMsg = _emailErrorMsg.asStateFlow()
    private var _hasPasswordError = MutableStateFlow(false)
    val hasPasswordError = _hasPasswordError.asStateFlow()
    private var _passwordErrorMsg = MutableStateFlow("")
    val passwordErrorMsg = _passwordErrorMsg.asStateFlow()
    private var _hasPasswordCheckError = MutableStateFlow(false)
    val hasPasswordCheckError = _hasPasswordCheckError.asStateFlow()
    private var _passwordCheckErrorMsg = MutableStateFlow("")
    val passwordCheckErrorMsg = _passwordCheckErrorMsg.asStateFlow()
    private var _signupResult = MutableStateFlow(false)
    val signupResult = _signupResult.asStateFlow()

    fun createAccount(email: String, password: String, passwordCheck: String) {
        if (!validateForm(email, password, passwordCheck)) return

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signupResult.value = true
                } else {
                    when (task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // TODO : toast_email_invalid_exception
                        }

                        is FirebaseAuthUserCollisionException -> {
                            // TODO : toast_email_collision_exception
                        }

                        else -> {
                            // TODO : msg_failed_signup
                        }
                    }
                }
            }
    }

    private fun validateForm(email: String, password: String, passwordConfirm: String): Boolean {
        return validateEmail(email) && validatePassword(password) && validatePasswordCheck(
            password,
            passwordConfirm
        )
    }

    private fun validateEmail(email: String): Boolean {
        if (TextUtils.isEmpty(email)) {
            setErrorMsg("필수 입력값입니다.", _emailErrorMsg, _hasEmailError)
            return false
        } else {
            if (!StringUtils.isValidEmail(email)) {
                setErrorMsg("이메일 형식이 아닙니다.", _emailErrorMsg, _hasEmailError)
                return false
            } else {
                setErrorMsg("", _emailErrorMsg, _hasEmailError)
            }
        }

        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (TextUtils.isEmpty(password)) {
            setErrorMsg("필수 입력값입니다.", _passwordErrorMsg, _hasPasswordError)
            return false
        } else {
            // 길이가 8자미만일때
            if (password.length < 8) {
                setErrorMsg("8자 이상 입력해주세요", _passwordErrorMsg, _hasPasswordError)
                return false
            } else if (!StringUtils.isValidPassword(password)) {
                // 8자 이상인데 영문자/숫자로 이루어지지 않았을 때
                setErrorMsg("영문자, 숫자로 입력해주세요.", _passwordErrorMsg, _hasPasswordError)
                return false
            } else {
                setErrorMsg("", _passwordErrorMsg, _hasPasswordError)
            }
        }

        return true
    }

    private fun validatePasswordCheck(password: String, passwordCheck: String): Boolean {
        if (TextUtils.isEmpty(passwordCheck)) {
            setErrorMsg("필수 입력값입니다.", _passwordCheckErrorMsg, _hasPasswordCheckError)
            return false
        } else {
            // 비밀번호와 비밀번호 확인이 일치하지 않을 때
            if (password != passwordCheck) {
                setErrorMsg("비밀번호와 일치하지 않습니다.", _passwordCheckErrorMsg, _hasPasswordCheckError)
                return false
            } else {
                setErrorMsg("", _passwordCheckErrorMsg, _hasPasswordCheckError)
            }
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