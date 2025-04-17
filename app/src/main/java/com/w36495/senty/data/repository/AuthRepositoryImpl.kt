package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
    override suspend fun signUpWithEmail(email: String, password: String): Result<Unit> {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.exception != null) {
                        cont.resume(Result.success(Unit))
                    } else {
                        cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }
}