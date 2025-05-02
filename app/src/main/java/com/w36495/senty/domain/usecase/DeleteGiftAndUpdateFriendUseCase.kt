package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import javax.inject.Inject

class DeleteGiftAndUpdateFriendUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val deleteGiftUseCase: DeleteGiftUseCase,
    private val updateFriendUseCase: UpdateFriendUseCase,
) {
    suspend operator fun invoke(gift: Gift): Result<Unit> {
        return deleteGiftUseCase(gift)
            .onSuccess {
                val friend = friendRepository.getFriend(gift.friendId).getOrThrow()

                updateFriendUseCase(
                    friend.copy(
                        received = if (gift.type == GiftType.RECEIVED) friend.received - 1 else friend.received,
                        sent = if (gift.type == GiftType.SENT) friend.sent - 1 else friend.sent
                    )
                ).onFailure {
                    Log.d("DeleteGiftAndUpdateFriendUseCase", "Error updating friend: ${it.message}")
                }

                Result.success(Unit)
            }
    }
}