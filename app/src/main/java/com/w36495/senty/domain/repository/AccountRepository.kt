package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.AccountPreference
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun userLogout(): Boolean
    fun deleteUser(): Boolean
    suspend fun setUserIdPreference(id: String, password: String)
    suspend fun getUserIdPreference(): Flow<AccountPreference>
    suspend fun hasSavedUserIdPreference(): Flow<Boolean>
    suspend fun clearUserIdPreference()
}