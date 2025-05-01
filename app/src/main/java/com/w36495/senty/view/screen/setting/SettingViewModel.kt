package com.w36495.senty.view.screen.setting

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.R
import com.w36495.senty.data.manager.CachedImageInfoManager
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.domain.repository.UserRepository
import com.w36495.senty.view.screen.setting.model.SettingContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _effect = Channel<SettingContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(SettingContact.State())
    val state get() = _state.asStateFlow()

    fun signOut(context: Context) {
        userRepository.user.value?.let {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                authRepository.signOut(it.loginType, context)
                    .onSuccess {
                        Log.d("SettingVM", "🟢 로그아웃 완료")
                        _state.update { it.copy(isLoading = false) }

                        userRepository.updateUser(null)
                        _effect.send(SettingContact.Effect.ShowToast(context.getString(R.string.settings_logout_complete_text)))
                    }
                    .onFailure {
                        Log.d("SettingVM", "🔴 로그아웃 실패")
                        _state.update { state -> state.copy(isLoading = false) }
                        _effect.send(SettingContact.Effect.ShowError(it))
                    }
            }
        }
    }

    fun withDraw(context: Context) {
        userRepository.user.value?.let {
            viewModelScope.launch {
                _state.update { it.copy(isLoading = true) }

                authRepository.withdraw(it.loginType, context)
                    .onSuccess {
                        Log.d("SettingVM", "🟢 회원탈퇴 완료")
                        _state.update { it.copy(isLoading = false) }

                        CachedImageInfoManager.clear()
                        userRepository.updateUser(null)
                        _effect.send(SettingContact.Effect.ShowToast(context.getString(R.string.settings_withdraw_complete_text)))
                    }
                    .onFailure {
                        _state.update { state -> state.copy(isLoading = false) }

                        Log.d("SettingVM", "🔴 회원탈퇴 실패")
                        _effect.send(SettingContact.Effect.ShowError(it))
                    }
            }
        }
    }

    fun sendEffect(effect: SettingContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}