package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.AuthUser
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val user: StateFlow<AuthUser?>

    fun getUid(): String
    fun updateUser(user: AuthUser?)

    suspend fun <T> runWithUid(block: suspend (uid: String) -> Result<T>): Result<T>
}