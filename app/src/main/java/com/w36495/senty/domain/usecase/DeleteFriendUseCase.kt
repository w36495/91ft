package com.w36495.senty.domain.usecase

import com.w36495.senty.domain.entity.Friend
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteFriendUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val giftRepository: GiftRepository,
    private val deleteGiftUseCase: DeleteGiftUseCase,
) {
    suspend operator fun invoke(friend: Friend): Result<Unit> {
        return friendRepository.deleteFriend(friend.id)
            .onSuccess {
                val gifts = giftRepository.getGiftsByFriend(friend.id).getOrElse { emptyList() }

                coroutineScope {
                    gifts.map { gift ->
                        async { deleteGiftUseCase(gift) }
                    }
                }.awaitAll()
            }
    }
}