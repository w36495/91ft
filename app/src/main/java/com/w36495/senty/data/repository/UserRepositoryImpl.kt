package com.w36495.senty.data.repository

import com.w36495.senty.domain.entity.AuthUser
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(

) : UserRepository {
    private var _user = MutableStateFlow<AuthUser?>(null)
    override val user: StateFlow<AuthUser?>
        get() = _user.asStateFlow()

    override fun getUid(): String {
        return _user.value?.uid ?: throw IllegalStateException("로그인된 사용자가 없습니다.")
    }

    override fun updateUser(user: AuthUser?) {
        _user.update { user }
    }

    override suspend fun <T> runWithUid(block: suspend (uid: String) -> Result<T>): Result<T> {
        return user
            .filterNotNull()
            .map { it.uid }
            .firstOrNull()
            ?.let { uid -> block(uid) }
            ?: Result.failure(IllegalStateException("로그인된 사용자가 없습니다."))
    }
}