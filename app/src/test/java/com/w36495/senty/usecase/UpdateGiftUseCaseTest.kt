package com.w36495.senty.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.datastore.FakeFriendSyncFlagDataStore
import com.w36495.senty.domain.usecase.UpdateFriendUseCase
import com.w36495.senty.domain.usecase.UpdateGiftUseCase
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateGiftUseCaseTest {
    private val giftRepository = FakeGiftRepository()
    private val friendRepository = FakeFriendRepository()
    private val friendSyncFlagDataStore = FakeFriendSyncFlagDataStore()

    private val updateFriendUseCase = UpdateFriendUseCase(friendRepository)
    private val useCase = UpdateGiftUseCase(giftRepository, friendRepository, updateFriendUseCase, friendSyncFlagDataStore)

    @Before
    fun setUp() {
        runBlocking {
            friendRepository.insertFriend(friend.toDomain())
        }
    }

    @Test
    fun `선물 타입을 받은 선물에서 준 선물로 변경하면, 친구 정보의 받은 선물 개수는 1 감소하고 준 선물 개수는 1 증가한다`() = runTest {
        giftRepository.insertGift(receivedGift.toDomain())
        friendRepository.patchFriend(friend.copy(received = friend.received+1).toDomain())

        // 현재 친구의 선물 개수
        val beforeFriend = friendRepository.getFriend(friend.id).getOrThrow()

        assertEquals(1, beforeFriend.received)
        assertEquals(0, beforeFriend.sent)

        // When : 선물 타입을 받은 선물에서 준 선물로 변경하면
        useCase(receivedGift.copy(type = GiftType.SENT).toDomain())

        // Then : 받은 선물 개수는 1 감소하고, 준 선물 개수는 1 증가한다
        val afterFriend = friendRepository.getFriend(friend.id).getOrThrow()

        assertEquals(0, afterFriend.received)
        assertEquals(1, afterFriend.sent)
    }

    @Test
    fun `선물 타입을 준 선물에서 받은 선물로 변경하면, 친구 정보의 준 선물 개수는 1 감소하고 받은 선물 개수는 1 증가한다`() = runTest {
        giftRepository.insertGift(sentGift.toDomain())
        friendRepository.patchFriend(friend.copy(sent = friend.sent+1).toDomain())

        // 현재 친구의 선물 개수
        val beforeFriend = friendRepository.getFriend(friend.id).getOrThrow()

        assertEquals(0, beforeFriend.received)
        assertEquals(1, beforeFriend.sent)

        // When : 선물 타입을 준 선물에서 받은 선물로 변경하면
        useCase(sentGift.copy(type = GiftType.RECEIVED).toDomain())

        // Then : 준 선물 개수는 1 감소하고, 받은 선물 개수는 1 증가한다
        val afterFriend = friendRepository.getFriend(friend.id).getOrThrow()

        assertEquals(1, afterFriend.received)
        assertEquals(0, afterFriend.sent)
    }

    @Test
    fun `선물 정보를 수정한 후, 친구 정보가 업데이트 되었을 때 친구 정보 동기화 flag는 null 이다`() = runTest {
        giftRepository.insertGift(sentGift.toDomain())
        friendRepository.patchFriend(friend.copy(sent = friend.sent+1).toDomain())

        // 현재 친구 정보 동기화 flag 확인
        val beforeSyncFlag = friendSyncFlagDataStore.load()

        assertEquals(null, beforeSyncFlag)

        // When : 친구 정보가 업데이트 되었을 때
        useCase(sentGift.copy(type = GiftType.RECEIVED).toDomain())

        // Then : 친구 정보 동기화 flag는 null이다
        val afterSyncFlag = friendSyncFlagDataStore.load()

        assertEquals(null, afterSyncFlag)
    }

    @Test
    fun `선물 정보를 수정한 후, 친구 정보가 업데이트 되지 못했을 때 친구 정보 동기화 flag 는 true 이다`() = runTest {
        giftRepository.insertGift(sentGift.copy(friendId = fakeFriend.id).toDomain())
        friendRepository.patchFriend(fakeFriend.toDomain())

        // 현재 친구 정보 동기화 flag 확인
        val beforeSyncFlag = friendSyncFlagDataStore.load()

        assertEquals(null, beforeSyncFlag)

        // When : 친구 정보가 업데이트 되지 못했을 때
        useCase(sentGift.copy(type = GiftType.RECEIVED).toDomain())

        // Then : 친구 정보 동기화 flag 는 true 이다
        val afterSyncFlag = friendSyncFlagDataStore.load()

        assertEquals(true, afterSyncFlag)
    }

    companion object {
        private val friend = FriendUiModel(id = "1", name = "Friend 1")
        private val fakeFriend = FriendUiModel(id = "2", name = "Fake Friend")
        private val receivedGift = GiftUiModel(id = "9", type = GiftType.RECEIVED, friendId = friend.id, friendName = friend.name)
        private val sentGift = GiftUiModel(id = "8", type = GiftType.SENT, friendId = friend.id, friendName = friend.name)
    }
}