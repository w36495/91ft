package com.w36495.senty.usecase

import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.usecase.UpdateFriendGroupUseCase
import com.w36495.senty.repository.FakeFriendGroupRepository
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateFriendGroupUseCaseTest {
    private val friendGroupRepository = FakeFriendGroupRepository()
    private val friendRepository = FakeFriendRepository()

    private val useCase = UpdateFriendGroupUseCase(
        friendGroupRepository,
        friendRepository,
    )

    @Before
    fun setUp() {
        runBlocking {
            friendGroups.forEach {
                friendGroupRepository.insertFriendGroup(it)
            }
            friendRepository.insertFriend(friend.toDomain())
        }
    }

    @Test
    fun `그룹명을 변경하면, 친구의 그룹명도 변경된다`() = runTest {
        // 친구의 그룹명 확인
        val friendGroup = friendGroupRepository.getFriendGroup(friendGroups.first().id).getOrThrow()
        val friend = friendRepository.getFriendsByFriendGroup(friendGroup.id).getOrThrow().first()

        assertEquals(friendGroup.name, friend.groupName)

        // When 그룹명을 변경하면
        useCase(friendGroup)

        // Then 친구의 그룹명도 변경된다
        val updatedFriendGroup = friendGroupRepository.getFriendGroup(friendGroup.id).getOrThrow()
        val updatedFriend = friendRepository.getFriendsByFriendGroup(updatedFriendGroup.id).getOrThrow().first()

        assertEquals(updatedFriendGroup.name, updatedFriend.groupName)
    }

    companion object {
        private val friendGroups = List(3) {
            FriendGroupUiModel(id = it.toString(), name = "Group $it").toDomain()
        }
        private val friend = FriendUiModel(
            id = "Friend 1",
            groupId = friendGroups.first().id,
            groupName = friendGroups.first().name,
        )
    }
}