package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.FriendKeyDTO
import com.w36495.senty.data.domain.GiftEntity
import com.w36495.senty.data.domain.GiftImgUriDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface GiftService {
    @POST("gifts/{userId}.json")
    suspend fun insertGift(
        @Path("userId") userId: String,
        @Body gift: GiftEntity
    ): Response<ResponseBody>

    @PATCH("gifts/{userId}/{giftKey}.json")
    suspend fun patchGiftKey(
        @Path("userId") userId: String,
        @Path("giftKey") giftKey: String,
        @Body body: FriendKeyDTO
    ): Response<ResponseBody>

    @PATCH("gifts/{userId}/{giftKey}.json")
    suspend fun patchGiftImgUri(
        @Path("userId") userId: String,
        @Path("giftKey") giftKey: String,
        @Body body: GiftImgUriDTO
    ): Response<ResponseBody>
}