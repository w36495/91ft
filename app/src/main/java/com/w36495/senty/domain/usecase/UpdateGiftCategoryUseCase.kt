package com.w36495.senty.domain.usecase

import com.w36495.senty.domain.entity.GiftCategory
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class UpdateGiftCategoryUseCase @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository,
    private val giftRepository: GiftRepository,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(category: GiftCategory): Result<Unit> {
        return mutex
            .withLock { giftCategoryRepository.updateCategory(category) }
            .onSuccess {
                giftRepository.getGiftsByCategoryId(category.id)
                    .onSuccess {
                        it.map { gift ->
                            giftRepository.updateGift(gift.copy(categoryId = category.id, categoryName = category.name))
                        }
                    }
            }
    }
}