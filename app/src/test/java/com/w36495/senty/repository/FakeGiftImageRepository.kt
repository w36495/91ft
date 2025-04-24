package com.w36495.senty.repository

import com.w36495.senty.domain.repository.GiftImgRepository

class FakeGiftImageRepository : GiftImgRepository {
    private val imageMap = mutableMapOf<String, MutableList<String>>() // giftId -> image list
    private var imageCounter = 0

    override suspend fun getGiftImages(giftId: String): Result<List<String>> {
        return Result.success(imageMap[giftId]?.toList() ?: emptyList())
    }

    override suspend fun insertGiftImageByBitmap(
        giftId: String,
        giftImage: ByteArray
    ): Result<Unit> {
        val imageName = "img_${imageCounter++}.jpg"
        val list = imageMap.getOrPut(giftId) { mutableListOf() }
        list.add(imageName)
        return Result.success(Unit)
    }

    override suspend fun deleteGiftImage(giftId: String, imgPath: String): Result<Unit> {
        val list = imageMap[giftId] ?: return Result.failure(Exception("Gift not found"))
        val removed = list.remove(imgPath)
        return if (removed) Result.success(Unit) else Result.failure(Exception("Image not found"))
    }

    override suspend fun deleteAllGiftImage(giftId: String): Result<Unit> {
        imageMap.remove(giftId)
        return Result.success(Unit)
    }

    fun getAllImages(): Map<String, List<String>> = imageMap
}