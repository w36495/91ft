package com.w36495.senty.domain.usecase

import com.w36495.senty.domain.entity.FriendGroup
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class UpdateFriendGroupUseCase @Inject constructor(
    private val friendGroupRepository: FriendGroupRepository,
    private val friendRepository: FriendRepository,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(friendGroup: FriendGroup): Result<Unit> {
        return try {
            mutex
                .withLock { friendGroupRepository.patchFriendGroup(friendGroup) }
                .onSuccess {
                    friendRepository.getFriendsByFriendGroup(friendGroup.id)
                        .onSuccess {
                            it.map { friend ->
                                friendRepository.patchFriend(friend.copy(groupName = friendGroup.name, groupColor = friendGroup.color))
                            }
                        }
                }
                .onFailure {
                    Result.failure<Unit>(it)
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}