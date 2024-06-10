package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendDetailEntity
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.FriendGroup
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    override fun getFriend(friendId: String): Flow<FriendDetail> = flow {
        val result = friendService.getFriend(userId, friendId)

        if (result.isSuccessful) {
            result.body()?.let {
                val jsonElement = Json.parseToJsonElement(it.string())
                val friendDetailEntity = Json.decodeFromJsonElement<FriendDetailEntity>(jsonElement.jsonObject)

                val friendGroup = FriendGroup.emptyFriendGroup.copy().apply { setId(friendDetailEntity.groupId) }
                val friendDetail = friendDetailEntity
                    .toDomainEntity()
                    .apply { setId(friendId) }
                    .copy(friendGroup = friendGroup)

                emit(friendDetail)
            }
        } else throw IllegalArgumentException(result.errorBody().toString())
    }

    override fun getFriends(): Flow<List<FriendDetail>> = flow {
        val result = friendService.getFriends(userId)

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())

                    responseJson.jsonObject.map { (key, jsonFriend) ->
                        val friendDetailEntity = Json.decodeFromJsonElement<FriendDetailEntity>(jsonFriend)
                        val friendGroup = FriendGroup.emptyFriendGroup.apply { setId(friendDetailEntity.groupId) }

                        friendDetailEntity
                            .toDomainEntity()
                            .copy(friendGroup = friendGroup.copy())
                            .apply { setId(key) }
                    }.let { friendDetails ->
                        emit(friendDetails.toList())
                    }
                }
            } else emit(emptyList())
        } else throw IllegalArgumentException(result.errorBody().toString())
    }

    override suspend fun insertFriend(friend: FriendDetailEntity): Response<ResponseBody> {
        return friendService.insertFriend(userId, friend)
    }

    override suspend fun patchFriend(friendId: String, friend: FriendDetailEntity): Response<ResponseBody> {
        return friendService.patchFriend(userId, friendId, friend)
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