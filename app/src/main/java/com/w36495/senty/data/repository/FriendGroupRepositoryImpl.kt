package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.data.remote.service.FriendGroupService
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
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

class FriendGroupRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val friendRepository: FriendRepository,
    private val friendGroupService: FriendGroupService,
) : FriendGroupRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid
    override fun getFriendGroup(friendGroupId: String): Flow<FriendGroup> = flow {
        val result = friendGroupService.getFriendGroup(userId, friendGroupId)

        if (result.isSuccessful) {
            result.body()?.let {
                val responseJson = Json.parseToJsonElement(it.string())

                Json.decodeFromJsonElement<FriendGroupEntity>(responseJson.jsonObject)
                    .toDomainModel()
                    .apply { setId(friendGroupId) }
                    .let { friendGroup -> emit(friendGroup) }
            }
        } else throw IllegalArgumentException("Failed to get friend group(${result.errorBody().toString()})")
    }

    override fun getFriendGroups(): Flow<List<FriendGroup>> = flow {
        val result = friendGroupService.getFriendGroups(userId)

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())

                    responseJson.jsonObject.map { (key, jsonElement) ->
                        Json.decodeFromJsonElement<FriendGroupEntity>(jsonElement).toDomainModel()
                            .apply { setId(key) }
                    }.let { friendGroups ->
                        emit(friendGroups.sortedBy { group-> group.name }.toList())
                    }
                }
            } else emit(emptyList())
        } else throw IllegalArgumentException("Failed to get friend groups(${result.errorBody().toString()})")
    }

    override suspend fun insertFriendGroup(friendGroupEntity: FriendGroupEntity): Boolean {
        val result = friendGroupService.insertFriendGroup(userId, friendGroupEntity)

        return result.isSuccessful
    }

    override suspend fun patchFriendGroup(friendKey: String, friendGroupEntity: FriendGroupEntity): Response<ResponseBody> {
        return friendGroupService.patchFriendGroup(userId, friendKey, friendGroupEntity)
    }

    override suspend fun deleteFriendGroup(friendGroupKey: String): Boolean {
        val result = friendGroupService.deleteFriendGroup(userId, friendGroupKey)

        if (result.isSuccessful) {
            coroutineScope {
                friendRepository.getFriends()
                    .map { friends ->
                        friends.filter { friend -> friend.friendGroup.id == friendGroupKey }
                    }
                    .collect { friends ->
                        friends.forEach {
                            friendRepository.deleteFriend(it.id)
                        }
                    }
            }

            return result.headers()["Content-length"]?.toInt() == 4
        } else throw IllegalArgumentException("Failed to delete friend group(${result.errorBody().toString()})")
    }
}