package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.repository.AccountRepositoryImpl
import com.w36495.senty.domain.repository.AccountRepository
import com.w36495.senty.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val accountRepository: AccountRepository,
) : ViewModel() {
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    private val _autoLogin = MutableStateFlow(false)
    val autoLogin = _autoLogin.asStateFlow()

    private var _result = MutableStateFlow(false)
    val result = _result.asStateFlow()

    init {
        checkSavedUserId()
    }

    private fun checkSavedUserId() {
        viewModelScope.launch {
            accountRepository.hasSavedUserIdPreference().collect { hasUserId ->
                if (hasUserId) {
                    accountRepository.getUserIdPreference()
                        .collect {
                            if (it.userId != AccountRepositoryImpl.PREFERENCE_DEFAULT && it.userPassword != AccountRepositoryImpl.PREFERENCE_DEFAULT) {
                                _autoLogin.update { true }
                            }
                        }
                }
            }
        }
    }

    fun userLogin(email: String, password: String, checkedAutoLogin: Boolean) {
        if (!isValid(email, password)) {
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (checkedAutoLogin) {
                        saveUserId(email, password)
                    }
                    setErrorMsg("성공적으로 로그인되었습니다.")
                    _result.value = true
                } else {
                    setErrorMsg("아이디 또는 비밀번호가 일치하지 않습니다.")
                    _result.value = false
                }
            }
    }

    private fun saveUserId(email: String, password: String) {
        viewModelScope.launch {
            accountRepository.setUserIdPreference(email, password)
        }
    }

    private fun isValid(email: String, password: String): Boolean {
        return validateEmail(email) && validatePassword(password)
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            setErrorMsg("이메일을 입력해주세요.")
            return false
        } else if (!StringUtils.isValidEmail(email)) {
            setErrorMsg("이메일 형식으로 입력해주세요.")
            return false
        }


        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            setErrorMsg("비밀번호를 입력해주세요.")
            return false
        }

        return true
    }

    private fun setErrorMsg(msg: String) {
        viewModelScope.launch {
            _errorFlow.emit(msg)
        }
    }
}