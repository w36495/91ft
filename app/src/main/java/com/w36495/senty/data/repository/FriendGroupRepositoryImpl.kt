package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.data.service.FriendGroupService
import com.w36495.senty.domain.FriendGroupRepository
import kotlinx.serialization.json.Json
import org.json.JSONObject
import javax.inject.Inject

class FriendGroupRepositoryImpl @Inject constructor(
    private val friendGroupService: FriendGroupService
) : FriendGroupRepository {
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override suspend fun getFriendGroups(): List<FriendGroupEntity> {
        val result = friendGroupService.getFriendGroups(userId)
        val friendGroups = mutableListOf<FriendGroupEntity>()

        if (result.isSuccessful) {
            result.body()?.let {
                val jsonObject = JSONObject(it.string())

                jsonObject.keys().forEach { key ->
                    val group = jsonObject[key] as JSONObject
                    val entity: FriendGroupEntity = Json.decodeFromString(group.toString())
                    entity.setId(key)
                    friendGroups.add(entity)
                }
            }
        } else throw IllegalArgumentException(result.errorBody().toString())

        return friendGroups.toList()
    }
}