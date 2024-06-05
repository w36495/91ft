package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.EntityKeyDTO
import com.w36495.senty.data.domain.FriendGroupEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path


interface FriendGroupService {
    @GET("friendGroups/{userId}/{friendGroupId}.json")
    suspend fun getFriendGroup(
        @Path("userId") userId: String,
        @Path("friendGroupId") friendGroupId: String,
    ): Response<ResponseBody>

    @GET("friendGroups/{userId}.json")
    suspend fun getFriendGroups(
        @Path("userId") userId: String,
    ): Response<ResponseBody>

    @POST("friendGroups/{userId}.json")
    suspend fun insertFriendGroup(
        @Path("userId") userId: String,
        @Body friendGroup: FriendGroupEntity
    ): Response<ResponseBody>

    @PATCH("friendGroups/{userId}/{friendGroupId}.json")
    suspend fun patchFriendGroup(
        @Path("userId") userId: String,
        @Path("friendGroupId") friendGroupId: String,
        @Body friendGroup: FriendGroupEntity
    ): Response<ResponseBody>

    @DELETE("friendGroups/{userId}/{friendGroupId}.json")
    suspend fun deleteFriendGroup(
        @Path("userId") userId: String,
        @Path("friendGroupId") friendGroupId: String,
    ): Response<String>
}