package com.w36495.senty.domain.usecase

import com.w36495.senty.domain.entity.Friend
import com.w36495.senty.domain.repository.FriendRepository
import javax.inject.Inject

class UpdateFriendUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
) {
    suspend operator fun invoke(friend: Friend): Result<Unit> {
        return friendRepository.patchFriend(friend)
    }
}