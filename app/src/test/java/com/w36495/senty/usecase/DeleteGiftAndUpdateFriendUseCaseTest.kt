package com.w36495.senty.usecase

import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.usecase.DeleteGiftAndUpdateFriendUseCase
import com.w36495.senty.domain.usecase.DeleteGiftUseCase
import com.w36495.senty.domain.usecase.UpdateFriendUseCase
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteGiftAndUpdateFriendUseCaseTest {
    private val friendRepository: FriendRepository = FakeFriendRepository()
    private val giftRepository: GiftRepository = FakeGiftRepository()
    private val giftImageRepository: GiftImageRepository = FakeGiftImageRepository()

    private val deleteGiftUseCase: DeleteGiftUseCase = DeleteGiftUseCase(giftRepository, giftImageRepository)
    private val updateFriendUseCase = UpdateFriendUseCase(friendRepository)
    private val useCase = DeleteGiftAndUpdateFriendUseCase(
        friendRepository = friendRepository,
        deleteGiftUseCase = deleteGiftUseCase,
        updateFriendUseCase = updateFriendUseCase,
    )
    @Before
    fun setUp() {
        runBlocking {
            friendRepository.fetchFriends()
            giftRepository.fetchGifts()

            // 데이터 추가
            friendRepository.insertFriend(friend.toDomain())
            giftRepository.insertGift(gift.toDomain())
            giftRepository.insertGift(sentGift.toDomain())

            repeat(3) {
                giftImageRepository.insertGiftImageByBitmap(gift.id, "testImage123", giftImage)
                giftImageRepository.insertGiftImageByBitmap(sentGift.id, "testImage1234", giftImage)
            }
        }
    }

    @Test
    fun `선물을 삭제하면, 이미지도 함께 삭제된다`() = runTest {
        // Given
        // 이미지 존재 여부 확인
        val originalGiftImages = giftImageRepository.getGiftImages(gift.id).getOrThrow()
        Assert.assertEquals(3, originalGiftImages.size)

        // When
        useCase(gift.toDomain())

        // Then
        val updatedGiftImages = giftImageRepository.getGiftImages(gift.id).getOrThrow()
        assertTrue(updatedGiftImages.isEmpty())
    }

    @Test
    fun `이미지가 없는 선물을 삭제하면, 선물만 삭제된다`() = runTest {
        // Given
        val originalFriend = friendRepository.getFriend(friendId = friend.id).getOrThrow()
        // 새로운 선물 추가 (이미지X)
        val newGift = GiftUiModel(id = "Gift 200", friendId = friend.id)
        giftRepository.insertGift(newGift.toDomain())

        assertEquals(13, giftRepository.gifts.value.size)

        // When : 선물 데이터 삭제
        val result = useCase(newGift.toDomain())
        assertEquals(12, giftRepository.gifts.value.size)

        // Then
        assertTrue(result.isSuccess)

        val updatedFriend = friendRepository.getFriend(gift.friendId).getOrThrow()
        assertEquals(originalFriend.received-1, updatedFriend.received)
    }

    @Test
    fun `받은 선물 1개를 삭제하면, 친구의 받은 선물의 개수가 1만큼 줄어든다`() = runTest {
        // Given
        val originalFriend = friendRepository.getFriend(friendId = friend.id).getOrThrow()
        val originalGiftImages = giftImageRepository.getGiftImages(gift.id).getOrThrow()

        // When : 선물을 삭제하면
        useCase(gift.toDomain())

        // Then : 해당 타입의 선물 개수가 줄어든다.
        val updatedFriend = friendRepository.getFriend(gift.friendId).getOrThrow()
        assertEquals(originalFriend.received-1, updatedFriend.received)
    }

    @Test
    fun `준 선물 1개를 삭제하면, 친구의 준 선물의 개수가 1만큼 줄어든다`() = runTest {
        // Given
        val originalFriend = friendRepository.getFriend(friendId = friend.id).getOrThrow()
        val originalGiftImages = giftImageRepository.getGiftImages(sentGift.id).getOrThrow()

        // When : 선물을 삭제하면
        useCase(sentGift.toDomain())

        // Then : 해당 타입의 선물 개수가 줄어든다.
        val updatedFriend = friendRepository.getFriend(gift.friendId).getOrThrow()
        Assert.assertEquals(originalFriend.sent-1, updatedFriend.sent)
    }

    companion object {
        private val friend = FriendUiModel(id = "Friend 100", received = 1, sent = 1)
        private val gift = GiftUiModel(id = "Gift 100", friendId = friend.id, type = GiftType.RECEIVED)
        private val sentGift = GiftUiModel(id = "Gift 101", friendId = friend.id, type = GiftType.SENT)
        private val giftImage = ByteArray(10) { 0 }
    }
}