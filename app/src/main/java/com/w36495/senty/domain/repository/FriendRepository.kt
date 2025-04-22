package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.Friend
import kotlinx.coroutines.flow.StateFlow

interface FriendRepository {
    val friends: StateFlow<List<Friend>>

    suspend fun getFriend(friendId: String): Result<Friend>
    suspend fun fetchFriends(): Result<Unit>
    suspend fun insertFriend(friend: Friend): Response<ResponseBody>
    suspend fun patchFriend(friendId: String, friend: Friend): Response<ResponseBody>
    suspend fun deleteFriend(friendId: String): Boolean
}