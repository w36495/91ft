package com.w36495.senty.viewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.w36495.senty.R
import com.w36495.senty.domain.entity.AuthUser
import com.w36495.senty.domain.entity.LoginType
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.domain.repository.UserRepository
import com.w36495.senty.view.screen.login.model.LoginEffect
import com.w36495.senty.view.screen.login.model.LoginFormState
import com.w36495.senty.view.screen.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val signInClient: SignInClient,
    private val signInRequest: BeginSignInRequest
) : ViewModel() {
    private val _formState = MutableStateFlow(LoginFormState())
    val formState get() = _formState.asStateFlow()

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState get() = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    private val _launchGoogleSignIn = MutableSharedFlow<IntentSenderRequest>()
    val launchGoogleSignIn = _launchGoogleSignIn.asSharedFlow()

    fun updateEmail(newEmail: String) {
        _formState.update { it.updateEmail(newEmail) }
    }

    fun updatePassword(newPassword: String) {
        _formState.update { it.updatePassword(newPassword) }
    }

    fun updateAutoLoginState(state: Boolean) {
        _formState.update { it.updateAuthLoginState(state) }
    }

    fun loginWithEmailAndPassword(context: Context) {
        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }

            val result = authRepository.signInWithEmail(formState.value.email, formState.value.password)

            result
                .onSuccess { firebaseUser ->
                    firebaseUser?.let { user ->
                        Log.d("LoginVM", "이메일 로그인 성공 ${user.uid}")
                        userRepository.updateUser(AuthUser(user.uid, LoginType.EMAIL))

                        _uiState.update { LoginUiState.Success }
                        _effect.send(LoginEffect.NavigateToHome)
                    }
                }
                .onFailure {
                    _effect.send(LoginEffect.ShowError(context.getString(R.string.login_error1)))
                }
        }
    }

    fun prepareSignInWithGoogle(context: Context) {
        signInClient.signOut()
        signInClient.beginSignIn(signInRequest)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(task.result.pendingIntent.intentSender)
                        .build()

                    viewModelScope.launch {
                        _launchGoogleSignIn.emit(intentSenderRequest)
                    }
                } else {
                    viewModelScope.launch {
                        _effect.send(LoginEffect.ShowError(context.getString(R.string.login_error1)))
                    }
                }
            }
    }

    fun loginWithGoogle(result: Intent?, context: Context) {
        val idToken = extractGoogleIdToken(result)

        if (idToken != null) {
            signInWithGoogleToken(idToken, context)
        } else {
            viewModelScope.launch {
                _effect.send(LoginEffect.ShowError(context.getString(R.string.login_error1)))
            }
        }
    }

    fun signInWithKakao(context: Context) {
        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }

            val result = authRepository.signInWithKakao(context)

            result
                .onSuccess {
                    Log.d("LoginVM", "카카오 로그인 성공")
                    userRepository.updateUser(it)

                    _uiState.update { LoginUiState.Success }
                    _effect.send(LoginEffect.NavigateToHome)
                }
                .onFailure {
                    _effect.send(LoginEffect.ShowError(context.getString(R.string.login_error1)))
                }
        }
    }

    private fun extractGoogleIdToken(result: Intent?): String? {
        return signInClient.getSignInCredentialFromIntent(result).googleIdToken
    }

    private fun signInWithGoogleToken(idToken: String, context: Context) {
        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }

            val result = authRepository.signInWithGoogle(idToken)

            result
                .onSuccess { firebaseUser ->
                    firebaseUser?.let { user ->
                        Log.d("LoginVM", "구글 로그인 성공 ${user.uid}")
                        userRepository.updateUser(AuthUser(user.uid, LoginType.GOOGLE))

                        _uiState.update { LoginUiState.Success }
                        _effect.send(LoginEffect.NavigateToHome)
                    }
                }
                .onFailure {
                    _effect.send(LoginEffect.ShowError(context.getString(R.string.login_error1)))
                }
        }
    }
}