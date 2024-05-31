package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.EntityKeyDTO
import com.w36495.senty.data.domain.GiftEntity
import com.w36495.senty.data.domain.GiftImgUriDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GiftService {
    @GET("gifts/{userId}/{giftId}.json")
    suspend fun getGift(
        @Path("userId") userId: String,
        @Path("giftId") giftId: String
    ): Response<ResponseBody>

    @GET("gifts/{userId}.json")
    suspend fun getGifts(
        @Path("userId") userId: String
    ): Response<ResponseBody>

    @POST("gifts/{userId}.json")
    suspend fun insertGift(
        @Path("userId") userId: String,
        @Body gift: GiftEntity
    ): Response<ResponseBody>

    @PATCH("gifts/{userId}/{giftKey}.json")
    suspend fun patchGiftKey(
        @Path("userId") userId: String,
        @Path("giftKey") giftKey: String,
        @Body body: EntityKeyDTO
    ): Response<ResponseBody>

    @PATCH("gifts/{userId}/{giftKey}.json")
    suspend fun patchGift(
        @Path("userId") userId: String,
        @Path("giftKey") giftKey: String,
        @Body body: GiftEntity
    ): Response<ResponseBody>

    @PATCH("gifts/{userId}/{giftKey}.json")
    suspend fun patchGiftImgUri(
        @Path("userId") userId: String,
        @Path("giftKey") giftKey: String,
        @Body body: GiftImgUriDTO
    ): Response<ResponseBody>

    @DELETE("gifts/{userId}/{giftKey}.json")
    suspend fun deleteGift(
        @Path("userId") userId: String,
        @Path("giftKey") giftKey: String
    ): Response<String>
}