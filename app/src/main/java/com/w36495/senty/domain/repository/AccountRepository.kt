package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.AccountPreference
import com.w36495.senty.data.domain.UserEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface AccountRepository {
    suspend fun userLogout(): Boolean
    fun deleteUser(): Boolean
    suspend fun insertUser(uid: String, user: UserEntity): Response<ResponseBody>
    suspend fun setUserIdPreference(id: String, password: String)
    suspend fun getUserIdPreference(): Flow<AccountPreference>
    suspend fun hasSavedUserIdPreference(): Flow<Boolean>
    suspend fun clearUserIdPreference()
}