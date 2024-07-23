package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.ProfileDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ProfileService {
    @GET("users/{uid}.json")
    suspend fun isInitialized(
        @Path("uid") uid: String
    ): Response<ResponseBody>

    @PATCH("users/{uid}.json")
    suspend fun patchInitialized(
        @Path("uid") uid: String,
        @Body profileDTO: ProfileDTO
    ): Response<ResponseBody>
}