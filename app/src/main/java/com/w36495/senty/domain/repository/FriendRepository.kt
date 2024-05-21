package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.FriendDetailEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface FriendRepository {
    fun getFriend(friendId: String): Flow<FriendDetailEntity>
    fun getFriends(): Flow<List<FriendDetailEntity>>
    suspend fun insertFriend(friend: FriendDetailEntity): Response<ResponseBody>
    suspend fun patchFriendId(friendId: String): Response<ResponseBody>
    suspend fun deleteFriend(friendId: String): Boolean
}