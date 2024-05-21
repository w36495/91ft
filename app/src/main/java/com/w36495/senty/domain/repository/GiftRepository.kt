package com.w36495.senty.domain.repository

import com.w36495.senty.view.entity.gift.GiftEntity
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftRepository {
    suspend fun insertGift(gift: GiftEntity): Response<ResponseBody>
    suspend fun patchGiftKey(giftKey: String): Response<ResponseBody>
}