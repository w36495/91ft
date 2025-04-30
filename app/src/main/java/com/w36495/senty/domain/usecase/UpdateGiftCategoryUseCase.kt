package com.w36495.senty.domain.usecase

import com.w36495.senty.domain.entity.GiftCategory
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import javax.inject.Inject

class UpdateGiftCategoryUseCase @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository,
    private val giftRepository: GiftRepository,
) {
    suspend operator fun invoke(category: GiftCategory): Result<Unit> {
        return giftCategoryRepository.updateCategory(category)
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