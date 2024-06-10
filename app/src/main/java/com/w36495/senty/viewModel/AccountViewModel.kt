package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
): ViewModel() {
    private var _logoutResult = MutableStateFlow(false)
    val logoutResult = _logoutResult.asStateFlow()
    private var _deleteUserResult = MutableStateFlow(false)
    val deleteUserResult = _deleteUserResult.asStateFlow()

    fun userLogout() {
        val result = accountRepository.userLogout()
        clearAutoLogin()
        _logoutResult.value = result
    }

    fun deleteUser() {
        val result = accountRepository.deleteUser()
        clearAutoLogin()
        _deleteUserResult.value = result
    }

    private fun clearAutoLogin() {
        viewModelScope.launch {
            accountRepository.clearUserIdPreference()
        }
    }
}