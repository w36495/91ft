package com.w36495.senty.domain.repository

interface GiftImageRepository {
    suspend fun getGiftThumbs(giftId: String, imageName: String): Result<String>
    suspend fun getGiftImages(giftId: String): Result<List<String>>
    suspend fun insertGiftImageByBitmap(giftId: String, imageName: String, image: ByteArray): Result<String>
    suspend fun deleteGiftImage(giftId: String, imgPath: String): Result<String>
    suspend fun deleteAllGiftImage(giftId: String): Result<Unit>
}