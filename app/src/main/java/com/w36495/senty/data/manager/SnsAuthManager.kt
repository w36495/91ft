package com.w36495.senty.data.manager

import android.content.Context
import com.w36495.senty.domain.entity.AuthUser

interface SnsAuthManager {
    suspend fun signIn(context: Context): Result<AuthUser>
    suspend fun signOut(context: Context): Result<Unit>
}