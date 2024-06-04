package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendDetailEntity
import com.w36495.senty.data.domain.EntityKeyDTO
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendService: FriendService,
) : FriendRepository {
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getFriend(friendId: String): Flow<FriendDetailEntity> = flow {
        val result = friendService.getFriend(userId, friendId)

        if (result.isSuccessful) {
            result.body()?.let {
                val responseJson = Json.parseToJsonElement(it.string())
                val parseFriend = Json.decodeFromJsonElement<FriendDetailEntity>(responseJson.jsonObject)

                emit(parseFriend)
            }
        } else throw IllegalArgumentException(result.errorBody().toString())
    }

    override fun getFriends(): Flow<List<FriendDetailEntity>> = flow {
        val result = friendService.getFriends(userId)
        val friends = mutableListOf<FriendDetailEntity>()

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())
                    responseJson.jsonObject.forEach { jsonFriend ->
                        val parseFriend = Json.decodeFromJsonElement<FriendDetailEntity>(jsonFriend.value)
                        friends.add(parseFriend)
                    }
                }
            }
        } else throw IllegalArgumentException(result.errorBody().toString())

        emit(friends)
    }

    override suspend fun insertFriend(friend: FriendDetailEntity): Response<ResponseBody> {
        return friendService.insertFriend(userId, friend)
    }

    override suspend fun patchFriendId(friendId: String): Response<ResponseBody> {
        val newId = EntityKeyDTO(id = friendId)

        return friendService.patchFriendKey(userId, friendId, newId)
    }

    override suspend fun patchFriend(friend: FriendDetailEntity): Response<ResponseBody> {
        return friendService.patchFriend(userId, friend.id, friend)
    }

    override suspend fun deleteFriend(friendId: String): Boolean {
        val result = friendService.deleteFriend(userId, friendId)

        if (result.isSuccessful) {
            return result.headers()["Content-length"]?.toInt() == 4
        } else throw IllegalArgumentException("Failed to delete friend")
    }
}