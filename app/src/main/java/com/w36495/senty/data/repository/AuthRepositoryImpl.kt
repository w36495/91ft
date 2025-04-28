package com.w36495.senty.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.w36495.senty.data.manager.SnsAuthManager
import com.w36495.senty.domain.entity.AuthUser
import com.w36495.senty.domain.entity.LoginType
import com.w36495.senty.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @Named("kakao") private val kakaoAuthManager: SnsAuthManager,
) : AuthRepository {
    override suspend fun checkLoginState(): Result<Boolean> {
        return try {
            val result = firebaseAuth.currentUser

            Result.success(result != null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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

    override suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser?> {
        return suspendCancellableCoroutine { cont ->
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(task.result.user))
                    } else {
                        cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser?> {
        return suspendCancellableCoroutine { cont ->
            val credential = GoogleAuthProvider.getCredential(idToken, null)

            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        cont.resume(Result.success(task.result.user))
                    } else {
                        cont.resume(Result.failure(task.exception ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override suspend fun signInWithKakao(context: Context): Result<AuthUser> {
        return kakaoAuthManager.signIn(context)
    }

    override suspend fun signOut(loginType: LoginType, context: Context): Result<Unit> {
        return when (loginType) {
            LoginType.EMAIL, LoginType.GOOGLE -> {
                firebaseAuth.signOut()
                Result.success(Unit)
            }

            LoginType.KAKAO -> {
                kakaoAuthManager.signOut(context)
            }
        }
    }
}
