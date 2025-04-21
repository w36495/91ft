package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.GiftCategoryEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GiftCategoryService {
    @GET("giftCategories/{userId}.json")
    suspend fun fetchCategories(
        @Path("userId") userId: String
    ): Response<ResponseBody>

    @POST("giftCategories/{userId}.json")
    suspend fun insertCategory(
        @Path("userId") userId: String,
        @Body category: GiftCategoryEntity
    ): Response<ResponseBody>

    @PATCH("giftCategories/{userId}/{categoryId}.json")
    suspend fun patchCategory(
        @Path("userId") userId: String,
        @Path("categoryId") categoryId: String,
        @Body category: GiftCategoryEntity
    ): Response<ResponseBody>

    @DELETE("giftCategories/{userId}/{categoryKey}.json")
    suspend fun deleteCategory(
        @Path("userId") userId: String,
        @Path("categoryKey") categoryKey: String
    ): Response<String>

}