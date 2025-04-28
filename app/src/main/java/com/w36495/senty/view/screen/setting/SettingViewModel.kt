package com.w36495.senty.view.screen.setting

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.R
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.domain.repository.UserRepository
import com.w36495.senty.view.screen.setting.model.SettingEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _effect = Channel<SettingEffect>()
    val effect = _effect.receiveAsFlow()

    fun signOut(context: Context) {
        userRepository.user.value?.let {
            viewModelScope.launch {
                authRepository.signOut(it.loginType, context)
                    .onSuccess {
                        userRepository.updateUser(null)
                        _effect.send(SettingEffect.ShowToast(context.getString(R.string.settings_logout_complete_text)))
                        Log.d("SettingVM", "🟢 로그아웃 완료")
                    }
                    .onFailure {
                        Log.d("SettingVM", "🔴 로그아웃 실패")
                    }
            }
        }
    }

    fun withDraw(context: Context) {
        userRepository.user.value?.let {
            viewModelScope.launch {
                authRepository.withdraw(it.loginType, context)
                    .onSuccess {
                        userRepository.updateUser(null)
                        _effect.send(SettingEffect.ShowToast(context.getString(R.string.settings_withdraw_complete_text)))
                        Log.d("SettingVM", "🟢 회원탈퇴 완료")
                    }
                    .onFailure {
                        Log.d("SettingVM", "🔴 회원탈퇴 실패")
                    }
            }
        }
    }

    fun sendEffect(effect: SettingEffect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}