package com.w36495.senty.usecase

import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.usecase.DeleteFriendGroupUseCase
import com.w36495.senty.domain.usecase.DeleteFriendUseCase
import com.w36495.senty.domain.usecase.DeleteGiftUseCase
import com.w36495.senty.repository.FakeFriendGroupRepository
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DeleteFriendGroupUseCaseTest {
    private val friendGroupRepository = FakeFriendGroupRepository()
    private val friendRepository = FakeFriendRepository()
    private val giftRepository = FakeGiftRepository()
    private val giftImageRepository = FakeGiftImageRepository()

    private val deleteGiftUseCase = DeleteGiftUseCase(
        giftRepository = giftRepository,
        giftImageRepository = giftImageRepository
    )

    private val deleteFriendUseCase = DeleteFriendUseCase(
        friendRepository = friendRepository,
        giftRepository = giftRepository,
        deleteGiftUseCase = deleteGiftUseCase,
    )

    private val useCase = DeleteFriendGroupUseCase(
        friendGroupRepository = friendGroupRepository,
        friendRepository = friendRepository,
        deleteFriendUseCase = deleteFriendUseCase,
    )

    @Before
    fun setUp() {

    }

    @Test
    fun `친구 그룹을 삭제하면 친구 그룹 목록에서 삭제된다`() = runTest {
        // Given
        friendGroupRepository.insertFriendGroup(newFriendGroup.toDomain())

        // 그룹 존재 여부 확인
        val beforeHasNewFriendGroup = friendGroupRepository.friendGroups.value.filter { it.id == newFriendGroup.id }
        Assert.assertTrue(beforeHasNewFriendGroup.isNotEmpty())

        // When : 친구 그룹을 삭제하면
        useCase(newFriendGroup.id)

        // Then : 친구 그룹 목록에서 삭제된다
        val afterHasNewFriendGroup = friendGroupRepository.friendGroups.value.filter { it.id == newFriendGroup.id }
        Assert.assertTrue(afterHasNewFriendGroup.isEmpty())
    }

    @Test
    fun `친구 그룹을 삭제하면, 해당 그룹의 친구도 함께 삭제된다`() = runTest {
        // Given
        friendGroupRepository.insertFriendGroup(newFriendGroup.toDomain())
        friendRepository.insertFriend(friend1.toDomain())
        friendRepository.insertFriend(friend2.toDomain())

        // 해당 그룹에 속하는 친구 리스트 존재 확인
        val hasFriends = friendRepository.getFriendsByFriendGroup(newFriendGroup.id).getOrElse { emptyList() }
        Assert.assertTrue(hasFriends.isNotEmpty())

        // When : 친구 그룹을 삭제하면
        useCase(newFriendGroup.id)

        // Then : 해당 그룹의 친구도 삭제된다
        val hasNoFriends = friendRepository.getFriendsByFriendGroup(newFriendGroup.id).getOrElse { emptyList() }
        Assert.assertTrue(hasNoFriends.isEmpty())

    }

    companion object {
        private val newFriendGroup = FriendGroupUiModel(id = "NewFriendGroup")
        private val friend1 = FriendUiModel(id = "Friend 20", groupId = newFriendGroup.id)
        private val friend2 = FriendUiModel(id = "Friend 21", groupId = newFriendGroup.id)
    }
}