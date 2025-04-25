package com.w36495.senty.domain.repository

interface GiftImageRepository {
    suspend fun getGiftThumbs(giftId: String, imageName: String): Result<String>
    suspend fun getGiftImages(giftId: String): Result<List<String>>
    suspend fun insertGiftImageByBitmap(giftId: String, imageName: String, image: ByteArray): Result<Unit>
    suspend fun deleteGiftImage(giftId: String, imgPath: String): Result<Unit>
    suspend fun deleteAllGiftImage(giftId: String): Result<Unit>
}