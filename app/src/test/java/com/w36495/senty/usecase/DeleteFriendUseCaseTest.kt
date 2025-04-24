package com.w36495.senty.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.usecase.DeleteFriendUseCase
import com.w36495.senty.domain.usecase.DeleteGiftUseCase
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DeleteFriendUseCaseTest {
    private val friendRepository = FakeFriendRepository()
    private val giftRepository = FakeGiftRepository()
    private val giftImageRepository = FakeGiftImageRepository()
    private val deleteGiftUseCase = DeleteGiftUseCase(
        giftRepository = giftRepository,
        giftImageRepository = giftImageRepository,
    )

    private val useCase = DeleteFriendUseCase(
        friendRepository = friendRepository,
        giftRepository = giftRepository,
        giftImageRepository = giftImageRepository,
        deleteGiftUseCase = deleteGiftUseCase,
    )

    @Before
    fun setUp() {
        runBlocking {
            friendRepository.fetchFriends()

            friendRepository.insertFriend(friend1.toDomain())
            friendRepository.insertFriend(friend2.toDomain())
        }
    }

    @Test
    fun `친구를 삭제하면, 친구 목록에서 삭제된다`() = runTest {
        val friends = friendRepository.friends.value

        // 삭제 전 친구 목록
        Assert.assertEquals(12, friends.size)

        // When : 친구를 삭제하면
        useCase(friend1.toDomain())

        // Then : 친구 목록에서 삭제된다
        Assert.assertEquals(11, friendRepository.friends.value.size)
    }

    @Test
    fun `선물을 가진 친구를 삭제하면, 선물도 함께 삭제된다`() = runTest {
        // Given : 선물을 가진 친구가 있다
        val newGift = GiftUiModel(id = "Gift 100", friendId = friend1.id, type = GiftType.RECEIVED)
        giftRepository.insertGift(newGift.toDomain())

        val beforeReceivedGiftCount = friend1.received
        Assert.assertEquals(1, beforeReceivedGiftCount)

        // When : 친구를 삭제하면
        useCase(friend1.toDomain())

        // Then : 선물도 함께 삭제된다
        // 친구는 존재하지 않음
        val friend = friendRepository.getFriend(friend1.id)
        Assert.assertTrue(friend.isFailure)

        // 선물에서도 해당 친구로 검색 불가능 (삭제됨)
        val gifts = giftRepository.getGiftsByFriend(friend1.id).getOrElse { emptyList() }
        Assert.assertTrue(gifts.isEmpty())
    }

    companion object {
        private val friend1 = FriendUiModel(id = "Friend 100", received = 1)
        private val friend2 = FriendUiModel(id = "Friend 200")
    }
}