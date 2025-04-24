package com.w36495.senty.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.usecase.DeleteGiftAndUpdateFriendUseCase
import com.w36495.senty.domain.usecase.DeleteGiftCategoryUseCase
import com.w36495.senty.domain.usecase.DeleteGiftUseCase
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftCategoryRepository
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteGiftCategoryUseCaseTest {
    private val giftCategoryRepository: GiftCategoryRepository = FakeGiftCategoryRepository()
    private val giftRepository: GiftRepository = FakeGiftRepository()
    private val giftImageRepository = FakeGiftImageRepository()
    private val friendRepository = FakeFriendRepository()

    private val deleteGiftUseCase = DeleteGiftUseCase(
        giftRepository = giftRepository,
        giftImageRepository = giftImageRepository,
    )

    private val deleteGiftAndUpdateFriendUseCase = DeleteGiftAndUpdateFriendUseCase(
        friendRepository = friendRepository,
        deleteGiftUseCase = deleteGiftUseCase,
    )

    private val useCase = DeleteGiftCategoryUseCase(
        giftRepository = giftRepository,
        giftImageRepository = giftImageRepository,
        giftCategoryRepository = giftCategoryRepository,
        deleteGiftAndUpdateFriendUseCase = deleteGiftAndUpdateFriendUseCase,
    )

    @Before
    fun setUp() {

        runBlocking {
            giftRepository.fetchGifts()
            giftCategoryRepository.fetchCategories()

            giftCategoryRepository.insertCategory(giftCategory.toDomain())
            friendRepository.insertFriend(friend.toDomain())
            giftRepository.insertGift(gift.toDomain())
            giftRepository.insertGift(sentGift.toDomain())
        }
    }

    @Test
    fun `선물 카테고리를 삭제하면, 해당 카테고리의 선물도 함께 삭제된다`() = runTest {
        // Given
        val gifts = giftRepository.getGiftsByCategoryId(giftCategory.id).getOrThrow()

        // 해당 카테고리에 속한 선물 데이터 여부 확인
        assertTrue(gifts.isNotEmpty())

        // When : 카테고리를 삭제한다
        useCase(giftCategory.toDomain())

        // Then : 해당 카테고리의 선물이 존재한다면, 함께 삭제된다
        // 카테고리 삭제 확인
        val remainingCategories = giftCategoryRepository.categories.value.filter { it.id == giftCategory.id }
        assertTrue(remainingCategories.isEmpty())

        // 선물 삭제 확인
        val remainingGifts = giftRepository.getGiftsByCategoryId(giftCategory.id).getOrThrow()
        assertTrue(remainingGifts.isEmpty())
    }

    @Test
    fun `선물 카테고리를 삭제했을 때, 해당 카테고리에 선물이 존재하지 않다면 카테고리만 삭제된다`() = runTest {
        // Given
        val newGiftCategory = GiftCategoryUiModel(id = "Category 200")
        val originalGifts = giftRepository.getGiftsByCategoryId(newGiftCategory.id).getOrElse { emptyList() }

        // 선물 중 삭제하려는 카테고리와 일치하는 선물이 존재하지 않음
        assertTrue(originalGifts.isEmpty())

        // When : 카테고리를 삭제한다
        useCase(giftCategory.toDomain())

        // Then : 해당 카테고리의 선물이 존재하지 않으므로, 카테고리만 삭제한다.
        val remainingCategories = giftCategoryRepository.categories.value.filter { it.id == newGiftCategory.id }
        assertTrue(remainingCategories.isEmpty())
    }

    companion object {
        private val friend = FriendUiModel(id = "Friend 100", received = 1, sent = 1)
        private val giftCategory = GiftCategoryUiModel(id = "Category 100")
        private val gift = GiftUiModel(id = "Gift 100", friendId = friend.id, type = GiftType.RECEIVED, categoryId = giftCategory.id)
        private val sentGift = GiftUiModel(id = "Gift 101", friendId = friend.id, type = GiftType.SENT, categoryId = giftCategory.id)
    }
}