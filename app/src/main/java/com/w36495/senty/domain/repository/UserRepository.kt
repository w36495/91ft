package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val user: StateFlow<AuthUser?>

    fun updateUser(user: AuthUser?)
}