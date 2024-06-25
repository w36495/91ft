package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.UserEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthService {
    @PUT("users/{uid}.json")
    suspend fun insertUser(
        @Path("uid") uid: String,
        @Body user: UserEntity
    ): Response<ResponseBody>
}