package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.data.domain.FriendKeyDTO
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendService: FriendService
) : FriendRepository {
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override suspend fun getFriends(): Flow<List<FriendEntity>> = flow {
        val result = friendService.getFriends(userId)
        val friends = mutableListOf<FriendEntity>()

        if (result.isSuccessful) {
            result.body()?.let {
                val jsonObject = JSONObject(it.string())
                jsonObject.keys().forEach { key ->
                    val jsonFriend = jsonObject[key] as JSONObject
                    val friend = Json.decodeFromString<FriendEntity>(jsonFriend.toString())
                    friend.setId(key)

                    friends.add(friend)
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