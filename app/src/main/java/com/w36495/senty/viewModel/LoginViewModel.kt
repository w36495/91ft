package com.w36495.senty.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.w36495.senty.data.domain.UserEntity
import com.w36495.senty.data.repository.AccountRepositoryImpl
import com.w36495.senty.domain.repository.AccountRepository
import com.w36495.senty.util.StringUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val accountRepository: AccountRepository,
    val signInClient: SignInClient,
    val signInRequest: BeginSignInRequest
) : ViewModel() {
    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow get() = _errorFlow.asSharedFlow()
    private var _result = MutableStateFlow(false)
    val result = _result.asStateFlow()
    var loading = mutableStateOf(false)
        private set
    var autoLogin = mutableStateOf(false)
        private set

    init {
        checkSavedUserId()
    }

    private fun checkSavedUserId() {
        viewModelScope.launch {
            accountRepository.hasSavedUserIdPreference()
                .collect { hasUserId ->
                    if (hasUserId) {
                        accountRepository
                            .getUserIdPreference()
                            .collectLatest {
                                if (it.userId != AccountRepositoryImpl.PREFERENCE_DEFAULT && it.userPassword != AccountRepositoryImpl.PREFERENCE_DEFAULT) {
                                    autoLogin.value = true
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

        loading.value = true

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (checkedAutoLogin) {
                        saveUserId(email, password)
                    }
                    loading.value = false
                    _result.value = true
                } else {
                    sendSnackbarMessage("아이디 또는 비밀번호가 일치하지 않습니다.")
                }
            }
    }

    fun signInWithGoogle(googleIdToken: String) {
        loading.value = true

        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        signInWithCredential(googleCredential)
    }

    fun signInWithFacebook(facebookToken: String) {
        loading.value = true

        val credential = FacebookAuthProvider.getCredential(facebookToken)
        signInWithCredential(credential)
    }

    fun sendSnackbarMessage(msg: String) {
        viewModelScope.launch {
            _errorFlow.emit(msg)
        }
    }

    private fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            try {
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false

                if (isNewUser) {
                    firebaseAuth.currentUser?.apply {
                        val user = UserEntity(
                            uid = uid,
                        )

                        coroutineScope {
                            val result = async { accountRepository.insertUser(uid, user) }.await()

                            if (result.isSuccessful) {
                                loading.value = false
                                _result.value = true
                            } else {
                                sendSnackbarMessage("로그인에 실패하였습니다.")
                            }
                        }
                    }
                } else {
                    loading.value = false
                    _result.value = true
                }
            } catch (firebaseAuthException: FirebaseAuthInvalidUserException) {
                sendSnackbarMessage("계정이 비활성화 되어있습니다.")
            } catch (firebaseAuthInvalidCredentialsException: FirebaseAuthInvalidCredentialsException) {
                // 자격 증명이 만료된 경우
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
            sendSnackbarMessage("이메일을 입력해주세요.")
            return false
        } else if (!StringUtils.isValidEmail(email)) {
            sendSnackbarMessage("이메일 형식으로 입력해주세요.")
            return false
        }


        return true
    }

    private fun validatePassword(password: String): Boolean {
        if (password.isEmpty()) {
            sendSnackbarMessage("비밀번호를 입력해주세요.")
            return false
        }

        return true
    }
}