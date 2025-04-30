package com.w36495.senty.usecase

import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.usecase.UpdateGiftCategoryUseCase
import com.w36495.senty.repository.FakeGiftCategoryRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateGiftCategoryUseCaseTest {
    private val giftCategoryRepository = FakeGiftCategoryRepository()
    private val giftRepository = FakeGiftRepository()

    private val useCase = UpdateGiftCategoryUseCase(
        giftCategoryRepository,
        giftRepository,
    )

    @Before
    fun setUp() {
        runBlocking {
            giftCategoryRepository.insertCategory(category.toDomain())
            giftRepository.insertGift(gift.toDomain())
        }
    }

    @Test
    fun `카테고리명을 변경하면, 선물의 카테고리명도 변경된다`() = runTest {
        val beforeCategory = giftCategoryRepository.fetchCategory(category.id).getOrThrow()
        val beforeGift = giftRepository.getGiftsByCategoryId(category.id).getOrThrow().first()

        assertEquals(beforeCategory.name, beforeGift.categoryName)

        // When 카테고리명을 변경하면
        useCase(beforeCategory)

        // Then 선물의 카테고리명도 변경된다
        val afterCategory = giftCategoryRepository.fetchCategory(category.id).getOrThrow()
        val afterGift = giftRepository.getGiftsByCategoryId(category.id).getOrThrow().first()

        assertEquals(afterCategory.name, afterGift.categoryName)
    }

    companion object {
        private val category = GiftCategoryUiModel(id = "1", name = "Category 1")
        private val gift = GiftUiModel(id = "999", categoryId = category.id, categoryName = category.name)
    }
}