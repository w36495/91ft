package com.w36495.senty.repository

import android.util.Log
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.entity.Friend
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeFriendRepository : FriendRepository {
    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    override val friends: StateFlow<List<Friend>>
        get() = _friends.asStateFlow()

    override suspend fun getFriend(friendId: String): Result<Friend> {
        return try {
            val friend = friends.value.first { it.id == friendId }
            Result.success(friend)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchFriends(): Result<Unit> {
        return try {
            _friends.update {
                List(10) {
                    FriendUiModel(id = "Friend $it", received = it).toDomain()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertFriend(friend: Friend): Result<Unit> {
        return try {
            _friends.update {
                it + friend
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun patchFriend(friend: Friend): Result<Unit> {
        return try {
            _friends.update {
                it.map { existing ->
                    if (existing.id == friend.id) friend else existing
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFriend(friendId: String): Result<Unit> {
        return try {
            _friends.update { friends ->
                friends.filterNot { it.id == friendId }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}