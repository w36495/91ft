package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.view.entity.gift.GiftDetail
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftRepository {
    fun getGift(giftId: String): Flow<GiftDetail>
    fun getGifts(): Flow<List<GiftDetail>>
    suspend fun insertGift(gift: Gift): Result<String>
    suspend fun updateGift(gift: Gift): Result<Unit>
    suspend fun patchGiftImgUri(giftKey: String, giftUri: String): Response<ResponseBody>
    suspend fun deleteGift(giftKey: String): Boolean
}