package com.w36495.senty.domain.usecase

import android.util.Log
import com.google.common.cache.Cache
import com.w36495.senty.data.manager.CachedImageInfoManager
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import javax.inject.Inject

class DeleteGiftUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImageRepository,
) {
    suspend operator fun invoke(gift: Gift): Result<Unit> {
        return try {
            giftRepository.deleteGift(gift.id)
                .onSuccess {
                    giftImageRepository.deleteAllGiftImage(gift.id)
                        .onSuccess {
                            gift.thumbnailName?.let {
                                CachedImageInfoManager.remove(it)
                            }
                        }
                        .onFailure {
                            Log.d("DeleteGiftUseCase", it.stackTraceToString())
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