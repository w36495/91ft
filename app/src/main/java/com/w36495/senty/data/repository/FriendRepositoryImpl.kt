package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.data.domain.FriendKeyDTO
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

    override fun getFriend(friendId: String): Flow<FriendEntity> = flow {
        val result = friendService.getFriend(userId, friendId)

        if (result.isSuccessful) {
            result.body()?.let {
                val responseJson = Json.parseToJsonElement(it.string())
                val parseFriend = Json.decodeFromJsonElement<FriendEntity>(responseJson.jsonObject)

                emit(parseFriend)
            }
        } else throw IllegalArgumentException(result.errorBody().toString())
    }

    override fun getFriends(): Flow<List<FriendEntity>> = flow {
        val result = friendService.getFriends(userId)
        val friends = mutableListOf<FriendEntity>()

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())
                    responseJson.jsonObject.forEach { jsonFriend ->
                        val parseFriend = Json.decodeFromJsonElement<FriendEntity>(jsonFriend.value)
                        friends.add(parseFriend)
                    }
                }
            }
        } else throw IllegalArgumentException(result.errorBody().toString())

        emit(friends)
    }

    override suspend fun insertFriend(friend: FriendEntity): Response<ResponseBody> {
        return friendService.insertFriend(userId, friend)
    }

    override suspend fun patchFriendId(friendId: String): Response<ResponseBody> {
        val newId = FriendKeyDTO(id = friendId)

        return friendService.patchFriendKey(userId, friendId, newId)
    }
}