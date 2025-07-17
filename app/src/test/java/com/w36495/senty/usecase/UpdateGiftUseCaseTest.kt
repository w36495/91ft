package com.w36495.senty.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.datastore.FakeFriendSyncFlagDataStore
import com.w36495.senty.domain.entity.SimpleFriend
import com.w36495.senty.domain.usecase.SaveGiftUseCase
import com.w36495.senty.domain.usecase.UpdateFriendUseCase
import com.w36495.senty.domain.usecase.UpdateGiftUseCase
import com.w36495.senty.domain.usecase.UploadGiftImageUseCase
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import com.w36495.senty.view.screen.gift.model.SimpleFriendUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateGiftUseCaseTest {
    private val giftRepository = FakeGiftRepository()
    private val giftImageRepository = FakeGiftImageRepository()
    private val friendRepository = FakeFriendRepository()
    private val friendSyncFlagDataStore = FakeFriendSyncFlagDataStore()

    private val updateFriendUseCase = UpdateFriendUseCase(friendRepository)
    private val uploadGiftImageUseCase = UploadGiftImageUseCase(giftImageRepository)
    private val saveGiftUseCase = SaveGiftUseCase(giftRepository, giftImageRepository, friendRepository, uploadGiftImageUseCase)
    private val useCase = UpdateGiftUseCase(giftRepository, friendRepository, updateFriendUseCase, friendSyncFlagDataStore)

    @Before
    fun setUp() {
        runBlocking {
            for (friend in friends) {
                friendRepository.insertFriend(friend)
            }
        }
    }

    @Test
    fun `선물 타입을 받은 선물에서 준 선물로 변경하면, 친구 정보의 받은 선물 개수는 1 감소하고 준 선물 개수는 1 증가한다`() = runTest {
        val gift = receivedGift.copy(friends = listOf(simpleFriend))
        saveGiftUseCase(gift.toDomain(), emptyMap())

        // 현재 친구의 선물 개수
        val beforeFriend = friendRepository.getFriend(simpleFriend.id).getOrThrow()

        // When : 선물 타입을 받은 선물에서 준 선물로 변경하면
        useCase(gift.copy(type = GiftType.SENT).toDomain())

        // Then : 받은 선물 개수는 1 감소하고, 준 선물 개수는 1 증가한다
        val afterFriend = friendRepository.getFriend(simpleFriend.id).getOrThrow()

        assertEquals(beforeFriend.received - 1, afterFriend.received)
        assertEquals(beforeFriend.sent + 1, afterFriend.sent)
    }

    @Test
    fun `선물 타입을 준 선물에서 받은 선물로 변경하면, 친구 정보의 준 선물 개수는 1 감소하고 받은 선물 개수는 1 증가한다`() = runTest {
        val gift = sentGift.copy(friends = listOf(simpleFriend))
        saveGiftUseCase(gift.toDomain(), emptyMap())

        // 현재 친구의 선물 개수
        val beforeFriend = friendRepository.getFriend(simpleFriend.id).getOrThrow()

        // When : 선물 타입을 준 선물에서 받은 선물로 변경하면
        useCase(gift.copy(type = GiftType.RECEIVED).toDomain())

        // Then : 준 선물 개수는 1 감소하고, 받은 선물 개수는 1 증가한다
        val afterFriend = friendRepository.getFriend(simpleFriend.id).getOrThrow()

        assertEquals(beforeFriend.received + 1, afterFriend.received)
        assertEquals(beforeFriend.sent - 1, afterFriend.sent)
    }

    @Test
    fun `선물 정보를 수정한 후, 친구 정보가 업데이트 되었을 때 친구 정보 동기화 flag는 null 이다`() = runTest {
        saveGiftUseCase(sentGift.toDomain(), emptyMap())
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
        val gift = sentGift.copy(friends = listOf(simpleFriend))
        saveGiftUseCase(gift.toDomain(), emptyMap())
        friendRepository.patchFriend(fakeFriend.toDomain())

        // 현재 친구 정보 동기화 flag 확인
        val beforeSyncFlag = friendSyncFlagDataStore.load()

        assertEquals(null, beforeSyncFlag)

        // When : 친구 정보가 업데이트 되지 못했을 때
        useCase(gift.copy(type = GiftType.RECEIVED).toDomain())

        // Then : 친구 정보 동기화 flag 는 true 이다
        val afterSyncFlag = friendSyncFlagDataStore.load()

        assertEquals(true, afterSyncFlag)
    }

    @Test
    fun `선물 타입이 변경되지 않고 친구가 추가되면, 추가된 친구의 gift count가 변경된다`() = runTest {
        saveGiftUseCase(sentGift.copy(friends = listOf(simpleFriend)).toDomain(), emptyMap())
        val beforeFriend2 = friendRepository.getFriend(simpleFriend2.id).getOrThrow()
        val beforeFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        // When : 친구를 1명에서 1명 이상으로 변경했을 때
        useCase(sentGift.copy(friends = listOf(simpleFriend, simpleFriend2, simpleFriend3)).toDomain())

        // Then : 추가된 친구들의 gift count가 변경된다
        val updatedFriend2 = friendRepository.getFriend(simpleFriend2.id).getOrThrow()
        val updatedFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        Assert.assertTrue(beforeFriend2.sent != updatedFriend2.sent)
        Assert.assertTrue(beforeFriend3.sent != updatedFriend3.sent)
        Assert.assertEquals(beforeFriend2.sent+1 , updatedFriend2.sent)
        Assert.assertEquals(beforeFriend3.sent+1 , updatedFriend3.sent)
    }

    @Test
    fun `선물 타입이 변경되지 않고 친구가 삭제되면, 삭제된 친구의 gift count가 변경된다`() = runTest {
        saveGiftUseCase(sentGift.copy(friends = listOf(simpleFriend, simpleFriend2, simpleFriend3)).toDomain(), emptyMap())
        val beforeFriend2 = friendRepository.getFriend(simpleFriend2.id).getOrThrow()
        val beforeFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        // When : 친구를 1명에서 1명 이상으로 변경했을 때
        useCase(sentGift.copy(friends = listOf(simpleFriend)).toDomain())

        // Then : 추가된 친구들의 gift count가 변경된다
        val updatedFriend2 = friendRepository.getFriend(simpleFriend2.id).getOrThrow()
        val updatedFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        Assert.assertTrue(beforeFriend2.sent != updatedFriend2.sent)
        Assert.assertTrue(beforeFriend3.sent != updatedFriend3.sent)
        Assert.assertEquals(beforeFriend2.sent-1 , updatedFriend2.sent)
        Assert.assertEquals(beforeFriend3.sent-1 , updatedFriend3.sent)
    }

    @Test
    fun `선물 타입과 친구 모두 변경되지 않으면 친구의 gift count는 유지된다`() = runTest {
        val gift = sentGift.copy(friends = listOf(simpleFriend, simpleFriend2))
        saveGiftUseCase(gift.toDomain(), emptyMap())

        val beforeFriend = friendRepository.getFriend(simpleFriend.id).getOrThrow()
        val beforeFriend2 = friendRepository.getFriend(simpleFriend2.id).getOrThrow()

        // When : 선물 타입과 친구 모두 변경되지 않으면
        useCase(gift.toDomain())

        // Then : 친구의 gift count는 유지된다
        val updatedFriend = friendRepository.getFriend(simpleFriend.id).getOrThrow()
        val updatedFriend2 = friendRepository.getFriend(simpleFriend2.id).getOrThrow()

        assertEquals(beforeFriend.sent, updatedFriend.sent)
        assertEquals(beforeFriend2.sent, updatedFriend2.sent)
    }

    @Test
    fun `선물 타입이 변경되면서 친구가 추가되면, 추가된 친구의 변경된 gift type의 count가 증가한다`() = runTest {
        val originGift = sentGift.copy(friends = listOf(simpleFriend, simpleFriend2))
        saveGiftUseCase(originGift.toDomain(), emptyMap())

        val beforeFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        // When : 선물 타입이 변경되면서 친구가 추가되면
        useCase(originGift.copy(type = GiftType.RECEIVED, friends = originGift.friends + simpleFriend3).toDomain())

        // Then : 추가된 친구의 변경된 gift type의 count가 증가한다
        val addedFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        Assert.assertEquals(1, addedFriend3.received)
        Assert.assertTrue(beforeFriend3.received != addedFriend3.received)
    }

    @Test
    fun `선물 타입이 변경되면서 친구가 삭제되면, 삭제된 친구의 변경 전 gift type의 count가 감소한다`() = runTest {
        val originGift = sentGift.copy(friends = listOf(simpleFriend, simpleFriend2, simpleFriend3))
        saveGiftUseCase(originGift.toDomain(), emptyMap())

        val beforeFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        // When : 선물 타입이 변경되면서 친구가 삭제되면
        useCase(originGift.copy(type = GiftType.RECEIVED, friends = listOf(simpleFriend, simpleFriend2)).toDomain())

        // Then : 삭제된 친구의 변경 전 gift type의 count가 감소한다
        val removedFriend3 = friendRepository.getFriend(simpleFriend3.id).getOrThrow()

        Assert.assertEquals(0, removedFriend3.sent)
        Assert.assertTrue(beforeFriend3.sent != removedFriend3.sent)
    }

    companion object {
        private val friend = FriendUiModel(id = "1", name = "Friend 1")
        private val friends = List(5) { FriendUiModel(id = it.toString(), name = "Friend $it").toDomain() }
        private val simpleFriend = SimpleFriendUiModel(id = "1", name = "Friend 1")
        private val simpleFriend2 = SimpleFriendUiModel(id = "2", name = "Friend 2")
        private val simpleFriend3 = SimpleFriendUiModel(id = "3", name = "Friend 3")
        private val fakeFriend = FriendUiModel(id = "2", name = "Fake Friend")
        private val receivedGift = GiftUiModel(id = "9", type = GiftType.RECEIVED, friendId = friend.id, friendName = friend.name)
        private val sentGift = GiftUiModel(id = "8", type = GiftType.SENT, friendId = friend.id, friendName = friend.name)
    }
}