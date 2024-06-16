package com.w36495.senty.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
): ViewModel() {
    private var _deleteUserResult = MutableStateFlow(false)
    val deleteUserResult = _deleteUserResult.asStateFlow()
    var logoutResult = mutableStateOf(false)
        private set

    fun userLogout() {
        clearAutoLogin()

        viewModelScope.launch {
            accountRepository.hasSavedUserIdPreference()
                .collectLatest { hasUserId ->
                    if (!hasUserId) {
                        try {
                            val result = async { accountRepository.userLogout() }

                            logoutResult.value = result.await()
                        } catch (exception: Exception) {
                            Log.d("AccountVM(Logout)", exception.message.toString())
                        }
                    }
                }
        }
    }

    fun deleteUser() {
        val result = accountRepository.deleteUser()
        clearAutoLogin()
        _deleteUserResult.update { result }
    }

    private fun clearAutoLogin() {
        viewModelScope.launch {
            accountRepository.clearUserIdPreference()
        }
    }
}