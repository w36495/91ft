package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import com.w36495.senty.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
): ViewModel() {
    private var _logoutResult = MutableStateFlow(false)
    val logoutResult = _logoutResult.asStateFlow()

    fun userLogout() {
        val result = accountRepository.userLogout()
        _logoutResult.value = result
    }
}