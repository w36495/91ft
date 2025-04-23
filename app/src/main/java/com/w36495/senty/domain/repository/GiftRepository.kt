package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.Gift
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftRepository {
    val gifts: StateFlow<List<Gift>>
    suspend fun getGift(giftId: String): Result<Gift>
    suspend fun fetchGifts(): Result<Unit>
    suspend fun insertGift(gift: Gift): Result<String>
    suspend fun updateGift(gift: Gift): Result<Unit>
    suspend fun patchGiftImgUri(giftKey: String, giftUri: String): Response<ResponseBody>
    suspend fun deleteGift(giftKey: String): Boolean
}