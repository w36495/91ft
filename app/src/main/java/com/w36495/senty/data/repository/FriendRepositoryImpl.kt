package com.w36495.senty.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEntity
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.domain.entity.Friend
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val friendService: FriendService,
    private val giftRepository: GiftRepository,
) : FriendRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid

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
                } else Result.success(Unit)
            } else {
                Log.d("FriendRepo", result.errorBody().toString())
                Result.failure(Exception(result.errorBody().toString()))
            }
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertFriend(friend: Friend): Response<ResponseBody> {
        return friendService.insertFriend(userId, friend.toEntity())
    }

    override suspend fun patchFriend(friendId: String, friend: Friend): Response<ResponseBody> {
        return friendService.patchFriend(userId, friendId, friend.toEntity())
    }

    override suspend fun deleteFriend(friendId: String): Boolean {
        val result = friendService.deleteFriend(userId, friendId)

        if (result.isSuccessful) {
            coroutineScope {
                giftRepository.getGifts()
                    .map { gifts ->
                        gifts.filter { it.friend.id == friendId }
                    }
                    .collect { gifts ->
                        gifts.forEach {
                            giftRepository.deleteGift(it.id)
                        }
                    }
            }

            return result.headers()["Content-length"]?.toInt() == 4
        } else throw IllegalArgumentException("Failed to delete friend")
    }
}