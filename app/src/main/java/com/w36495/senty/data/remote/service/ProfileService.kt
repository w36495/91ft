package com.w36495.senty.data.remote.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileService {
    @GET("users/{uid}.json")
    suspend fun isInitialized(
        @Path("uid") uid: String
    ): Response<ResponseBody>
}