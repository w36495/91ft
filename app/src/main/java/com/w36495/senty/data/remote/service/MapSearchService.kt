package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.MapSearchDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapSearchService {
    @GET("geocode")
    suspend fun getSearch(
        @Header("X-NCP-APIGW-API-KEY-ID") clientKey: String,
        @Header("X-NCP-APIGW-API-KEY") secretKey: String,
        @Query("query") query: String,
    ): Response<MapSearchDTO>
}