package com.w36495.senty.domain.repository

interface GiftImgRepository {
    suspend fun getGiftImages(giftId: String): Result<List<String>>
    suspend fun insertGiftImageByBitmap(giftId: String, giftImage: ByteArray): Result<Unit>
    suspend fun deleteGiftImage(giftId: String, imgPath: String): Result<Unit>
    suspend fun deleteAllGiftImage(giftId: String): Result<Unit>
}