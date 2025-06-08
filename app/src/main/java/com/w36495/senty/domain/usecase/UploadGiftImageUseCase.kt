package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.domain.entity.EditImage
import com.w36495.senty.domain.error.EditGiftError
import com.w36495.senty.domain.repository.GiftImageRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class UploadGiftImageUseCase @Inject constructor(
    private val giftImageRepository: GiftImageRepository,
) {
    suspend operator fun invoke(giftId: String, images: Map<String, EditImage>): Result<Unit> {
        return try {
            val newImages = images.entries.filter { it.value is EditImage.New }

            val uploadResults = coroutineScope {
                newImages.map { (imageName, image) ->
                    async { giftImageRepository.insertGiftImageByBitmap(giftId, imageName, (image as EditImage.New).byteArray) }
                }.awaitAll()
            }

            val failures = uploadResults.filter { it.isFailure }

            return if (failures.isNotEmpty()) {
                val failedImageNames = failures.map { it.getOrThrow() }

                val deleteResults = coroutineScope {
                    failedImageNames.map { imageName ->
                        async { giftImageRepository.deleteGiftImage(giftId, imageName) }
                    }.awaitAll()
                }

                deleteResults.forEach {
                    if (it.isFailure) {
                        Log.d("UploadGiftImageUseCase", "이미지 삭제 실패: ${it.getOrThrow()}")
                    }
                }

                Result.failure(
                    EditGiftError.ImageUploadFailed(
                        "이미지 업로드 실패: ${failedImageNames.joinToString(", ")}"
                    )
                )

            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(EditGiftError.ImageUploadFailed(e.message ?: "이미지 업로드 실패"))
        }
    }
}