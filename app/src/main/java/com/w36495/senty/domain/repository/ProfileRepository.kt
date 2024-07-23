package com.w36495.senty.domain.repository

import okhttp3.ResponseBody
import retrofit2.Response

interface ProfileRepository {
    suspend fun isInitialized(): Response<ResponseBody>
    suspend fun patchInitialized(): Response<ResponseBody>
}