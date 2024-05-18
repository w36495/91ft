package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.FriendEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface FriendRepository {
    fun getFriend(friendId: String): Flow<FriendEntity>
    fun getFriends(): Flow<List<FriendEntity>>
    suspend fun insertFriend(friend: FriendEntity): Response<ResponseBody>
    suspend fun patchFriendId(friendId: String): Response<ResponseBody>
}