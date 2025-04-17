package com.w36495.senty.data.manager

import android.content.Context
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.w36495.senty.domain.entity.AuthUser
import com.w36495.senty.domain.entity.LoginType
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class KakaoAuthManager @Inject constructor(
    private val userRepository: UserRepository,
) : SnsAuthManager {
    override suspend fun signIn(context: Context): Result<AuthUser> {
        return suspendCancellableCoroutine { cont ->
            val loginCallback = { token: OAuthToken?, error: Throwable? ->
                if (error != null) {
                    cont.resume(Result.failure(error))
                } else {
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            cont.resume(Result.failure(error))
                        } else if (user != null) {
                            val uid = "kakao_${user.id}"
                            val authUser = AuthUser(uid, LoginType.KAKAO)
                            userRepository.updateUser(authUser)
                            cont.resume(Result.success(authUser))
                        }
                    }
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                UserApiClient.instance.loginWithKakaoTalk(context, callback = loginCallback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(context, callback = loginCallback)
            }
        }
    }

    override suspend fun signOut(context: Context): Result<Unit> {
        return suspendCancellableCoroutine { cont ->
            UserApiClient.instance.logout { error ->
                if (error != null) cont.resume(Result.failure(error))
                else cont.resume(Result.success(Unit))
            }
        }
    }
}