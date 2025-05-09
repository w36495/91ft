package com.w36495.senty.domain.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.w36495.senty.domain.entity.AuthUser
import com.w36495.senty.domain.entity.LoginType

interface AuthRepository {
    suspend fun checkLoginState(): Result<AuthUser?>
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit>
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?>
    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?>
    suspend fun signInWithKakao(context: Context): Result<AuthUser>
    suspend fun signOut(loginType: LoginType, context: Context): Result<Unit>
    suspend fun withdraw(loginType: LoginType, context: Context): Result<Unit>
}