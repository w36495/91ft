package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.view.entity.FriendGroup
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface FriendGroupRepository {
    fun getFriendGroup(friendGroupId: String): Flow<FriendGroup>
    fun getFriendGroups(): Flow<List<FriendGroup>>
    suspend fun insertFriendGroup(friendGroupEntity: FriendGroupEntity): Boolean
    suspend fun patchFriendGroup(friendKey: String, friendGroupEntity: FriendGroupEntity): Response<ResponseBody>
    suspend fun deleteFriendGroup(friendGroupKey: String): Boolean
}