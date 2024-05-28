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
class LoginViewModel @Inject constructor(
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
    private var _result = MutableStateFlow(false)
    val result = _result.asStateFlow()

    fun userLogin(email: String, password: String) {
        if (!isValid(email, password)) {
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _result.value = true
//                    val userSharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
//                    with(userSharedPref.edit()) {
//                        putString("userId", FirebaseAuth.getInstance().currentUser!!.uid)
//                        commit()
//                    }
                } else _result.value = false
            }
    }

    private fun isValid(email: String, password: String): Boolean {
        return validateEmail(email) && validatePassword(password)
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            setErrorMsg("이메일을 입력해주세요.", _emailErrorMsg, _hasEmailError)
            return false
        } else {
            if (!StringUtils.isValidEmail(email)) {
                setErrorMsg("이메일 형식으로 입력해주세요.", _emailErrorMsg, _hasEmailError)
                return false
            } else {
                setErrorMsg("", _emailErrorMsg, _hasEmailError)
            }
        }

        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            setErrorMsg("비밀번호를 입력해주세요.", _passwordErrorMsg, _hasPasswordError)
            return false
        } else {
            setErrorMsg("", _passwordErrorMsg, _hasPasswordError)
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