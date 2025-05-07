package com.w36495.senty.data.repository

import android.util.Log
import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEntity
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.domain.entity.Friend
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendService: FriendService,
    private val userRepository: UserRepository,
) : FriendRepository {
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

    override suspend fun getFriendsByFriendGroup(friendGroupId: String): Result<List<Friend>> {
        return try {
            val friends = friends.value.filter { it.groupId == friendGroupId }
            Result.success(friends)
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun fetchFriends(): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val result = friendService.getFriends(userId)

                if (result.isSuccessful) {
                    val body = result.body()?.string()

                    if (body != null && result.headers()["Content-length"]?.toInt() != 4) {
                        val responseJson = Json.parseToJsonElement(body)

                        val friends = responseJson.jsonObject.map { (key, jsonFriend) ->
                            Json.decodeFromJsonElement<FriendEntity>(jsonFriend).toDomain(key)
                        }.toList()

                        Log.d("FriendRepository", "친구 목록 조회 완료")
                        _friends.update { friends }
                        Result.success(Unit)
                    } else {
                        _friends.update { emptyList() }
                        Result.success(Unit)
                    }
                } else {
                    Log.d("FriendRepo", result.errorBody().toString())
                    Result.failure(Exception(result.errorBody().toString()))
                }
            }
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertFriend(friend: Friend): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = friendService.insertFriend(userId, friend.toEntity())

                if (response.isSuccessful) {
                    fetchFriends()
                    Result.success(Unit)
                } else Result.failure(Exception("친구 등록 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun patchFriend(friend: Friend): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = friendService.patchFriend(userId, friend.id, friend.toEntity())

                if (response.isSuccessful) {
                    fetchFriends()
                    Result.success(Unit)
                } else Result.failure(Exception("친구 수정 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFriend(friendId: String): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = friendService.deleteFriend(userId, friendId)

                if (response.isSuccessful) {
                    fetchFriends()
                    Log.d("FriendRepo", "친구 삭제 성공")
                    Result.success(Unit)
                } else {
                    Log.d("FriendRepo", "친구 삭제 실패")
                    Result.failure(Exception("친구 삭제 실패"))
                }
            }
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}