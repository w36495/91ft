package com.w36495.senty.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface FriendGroupService {
    @GET("friendGroups/{userId}.json")
    suspend fun getFriendGroups(
        @Path("userId") userId: String,
    ): Response<ResponseBody>
}