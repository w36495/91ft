package com.w36495.senty.data.repository

import com.w36495.senty.domain.entity.AuthUser
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(

) : UserRepository {
    private var _user = MutableStateFlow<AuthUser?>(null)
    override val user: StateFlow<AuthUser?>
        get() = _user.asStateFlow()

    override fun updateUser(user: AuthUser) {
        _user.update { user }
    }
}