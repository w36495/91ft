package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.Friend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody
import retrofit2.Response

interface FriendRepository {
    val friends: StateFlow<List<Friend>>

    fun getFriend(friendId: String): Flow<Friend>
    suspend fun fetchFriends(): Result<Unit>
    suspend fun insertFriend(friend: Friend): Response<ResponseBody>
    suspend fun patchFriend(friendId: String, friend: Friend): Response<ResponseBody>
    suspend fun deleteFriend(friendId: String): Boolean
}