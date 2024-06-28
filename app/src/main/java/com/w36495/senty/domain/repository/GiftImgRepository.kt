package com.w36495.senty.domain.repository

interface GiftImgRepository {
    suspend fun getGiftImages(giftId: String): List<String>
    suspend fun insertGiftImgByBitmap(giftId: String, giftImage: ByteArray)
    suspend fun deleteGiftImg(giftId: String, imgPath: String): Boolean
}