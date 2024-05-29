package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.FriendGroupEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface FriendGroupRepository {
    fun getFriendGroups(): Flow<List<FriendGroupEntity>>
    suspend fun insertFriendGroup(friendGroupEntity: FriendGroupEntity): Boolean
    suspend fun patchFriendGroupKey(friendGroupKey: String): Response<ResponseBody>
    suspend fun patchFriendGroup(friendGroupEntity: FriendGroupEntity): Response<ResponseBody>
    suspend fun deleteFriendGroup(friendGroupKey: String): Boolean
}