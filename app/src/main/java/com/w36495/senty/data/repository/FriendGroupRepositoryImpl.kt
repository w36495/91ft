package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.EntityKeyDTO
import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.data.remote.service.FriendGroupService
import com.w36495.senty.domain.repository.FriendGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class FriendGroupRepositoryImpl @Inject constructor(
    private val friendGroupService: FriendGroupService
) : FriendGroupRepository {
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun getFriendGroups(): Flow<List<FriendGroupEntity>> = flow {
        val result = friendGroupService.getFriendGroups(userId)
        val friendGroups = mutableListOf<FriendGroupEntity>()

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val jsonObject = JSONObject(it.string())

                    jsonObject.keys().forEach { key ->
                        val group = jsonObject[key] as JSONObject
                        val entity: FriendGroupEntity = Json.decodeFromString(group.toString())
                        friendGroups.add(entity)
                    }
                }
            }
        } else throw IllegalArgumentException("Failed to get friend groups(${result.errorBody().toString()})")

        emit(friendGroups)
    }

    override suspend fun insertFriendGroup(friendGroupEntity: FriendGroupEntity): Boolean {
        val result = friendGroupService.insertFriendGroup(userId, friendGroupEntity)

        if (result.isSuccessful) {
            result.body()?.let {
                val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                val key = jsonObject["name"].toString().replace("\"", "")

                val groupKeyResult = patchFriendGroupKey(key)
                if (groupKeyResult.isSuccessful) {
                    return true
                }
            }
        }

        return false
    }

    override suspend fun patchFriendGroupKey(friendGroupKey: String): Response<ResponseBody> {
        val newKey = EntityKeyDTO(friendGroupKey)

        return friendGroupService.patchFriendGroupKey(userId, friendGroupKey, newKey)
    }

    override suspend fun patchFriendGroup(friendGroupEntity: FriendGroupEntity): Response<ResponseBody> {
        return friendGroupService.patchFriendGroup(userId, friendGroupEntity.id, friendGroupEntity)
    }

    override suspend fun deleteFriendGroup(friendGroupKey: String): Boolean {
        val result = friendGroupService.deleteFriendGroup(userId, friendGroupKey)

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() == 4) return true
        }

        return false
    }
}