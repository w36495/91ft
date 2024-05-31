package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.GiftEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftRepository {
    fun getGift(giftId: String): Flow<GiftEntity>
    fun getGifts(): Flow<List<GiftEntity>>
    suspend fun insertGift(gift: GiftEntity): Response<ResponseBody>
    suspend fun patchGiftKey(giftKey: String): Response<ResponseBody>
    suspend fun patchGift(gift: GiftEntity): Response<ResponseBody>
    suspend fun patchGiftImgUri(giftKey: String, giftUri: String): Response<ResponseBody>
    suspend fun deleteGift(giftKey: String): Boolean
}