package com.w36495.senty.domain.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteGiftAndUpdateFriendUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val deleteGiftUseCase: DeleteGiftUseCase,
    private val updateFriendUseCase: UpdateFriendUseCase,
) {
    suspend operator fun invoke(gift: Gift): Result<Unit> {
        return deleteGiftUseCase(gift)
            .onSuccess {
                val removedFriendIds = gift.friends.map { it.id }.toSet()

                coroutineScope {
                    async { removeFriends(gift.type, removedFriendIds) }
                }.await()

                Unit
            }
    }

    private suspend fun removeFriends(type: GiftType, friendIds: Set<String>) = coroutineScope {
        friendIds.map { friendId ->
            async {
                val friend = friendRepository.getFriend(friendId).getOrThrow()

                updateFriendUseCase(
                    friend.copy(
                        received = if (type == GiftType.RECEIVED) friend.received - 1 else friend.received,
                        sent = if (type == GiftType.SENT) friend.sent - 1 else friend.sent
                    )
                )
            }
        }
    }.awaitAll()
}