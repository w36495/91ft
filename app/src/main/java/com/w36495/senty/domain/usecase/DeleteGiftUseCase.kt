package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteGiftUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImgRepository,
) {
    suspend operator fun invoke(gift: Gift, images: List<String>): Result<Unit> {
        return try {
            giftRepository.deleteGift(gift.id)
                .onSuccess {
                    val results = coroutineScope {
                        images.map { image ->
                            val parseImagePath = image
                                .substringAfterLast("/") // 전체 경로에서 마지막 segment 추출
                                .substringBefore("?") // 쿼리 제거
                                .substringAfterLast("%2F") // Firebase Storage 경로 추출
                            async { giftImageRepository.deleteGiftImage(gift.id, parseImagePath) }
                        }
                    }.awaitAll()

                    results.forEach {
                        it.onFailure {
                            Log.d("DeleteGiftUseCase", it.stackTraceToString())
                        }
                    }

                    Result.success(Unit)
                }
                .onFailure {
                    Log.d("DeleteGiftUseCase", it.stackTraceToString())
                    Result.failure<Unit>(it)
                }
        } catch (e: Exception) {
            Log.d("DeleteGiftUseCase", e.stackTraceToString())
            Result.failure(e)
        }
    }
}