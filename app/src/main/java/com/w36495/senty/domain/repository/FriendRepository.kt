package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.FriendDetailEntity
import com.w36495.senty.view.entity.FriendDetail
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface FriendRepository {
    fun getFriend(friendId: String): Flow<FriendDetail>
    fun getFriends(): Flow<List<FriendDetail>>
    suspend fun insertFriend(friend: FriendDetailEntity): Response<ResponseBody>
    suspend fun patchFriend(friend: FriendDetailEntity): Response<ResponseBody>
    suspend fun deleteFriend(friendId: String): Boolean
}