package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.data.domain.FriendKeyDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface FriendService {
    @POST("friends/{userId}.json")
    suspend fun insertFriend(
        @Path("userId") userId: String,
        @Body friend: FriendEntity
    ): Response<ResponseBody>

    @PATCH("friends/{userId}/{friendId}.json")
    suspend fun updateFriendKey(
        @Path("userId") userId: String,
        @Path("friendId") friendId: String,
        @Body body: FriendKeyDTO
    ): Response<ResponseBody>
}