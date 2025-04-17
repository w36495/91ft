package com.w36495.senty.domain.repository

interface AuthRepository {
    suspend fun signUpWithEmail(email: String, password: String): Result<Unit>
}