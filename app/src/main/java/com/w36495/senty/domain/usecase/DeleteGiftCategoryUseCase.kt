package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.domain.entity.GiftCategory
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteGiftCategoryUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftCategoryRepository: GiftCategoryRepository,
    private val deleteGiftAndUpdateFriendUseCase: DeleteGiftAndUpdateFriendUseCase,
) {
    suspend operator fun invoke(category: GiftCategory): Result<Unit> {
        return giftCategoryRepository.deleteCategory(category.id)
            .onSuccess {
                val gifts = giftRepository.getGiftsByCategoryId(categoryId = category.id).getOrElse { emptyList() }

                val results = coroutineScope {
                    gifts.map { gift ->
                        async { deleteGiftAndUpdateFriendUseCase(gift) }
                    }
                }.awaitAll()

                results.forEach {
                    it.onFailure {
                        Log.d("DeleteGiftCategoryUseCase", it.stackTraceToString())
                    }
                }

                Result.success(Unit)
            }
            .onFailure {
                Result.failure<Unit>(it)
            }
    }
}