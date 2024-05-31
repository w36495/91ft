package com.w36495.senty.domain.repository

interface GiftImgRepository {
    suspend fun getGiftImages(giftId: String, imgPath: String): String
    suspend fun insertGiftImgByBitmap(giftId: String, giftImg: String): String
    suspend fun deleteGiftImg(imgPath: String): Boolean
}