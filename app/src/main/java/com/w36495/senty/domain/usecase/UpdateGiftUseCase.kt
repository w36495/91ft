package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import javax.inject.Inject

class UpdateGiftUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val friendRepository: FriendRepository,
    private val updateFriendUseCase: UpdateFriendUseCase,
) {
    suspend operator fun invoke(gift: Gift): Result<Unit> {
        val beforeGift = giftRepository.getGift(gift.id).getOrThrow()
        val isDifferentGiftType = beforeGift.type != gift.type

        return giftRepository.updateGift(gift)
            .onSuccess {
                // 친구 정보 업데이트
                if (isDifferentGiftType) {
                    val friend = friendRepository.getFriend(gift.friendId).getOrThrow()

                    updateFriendUseCase(
                        friend.copy(
                             received = if (gift.type == GiftType.RECEIVED) friend.received + 1 else friend.received - 1,
                            sent = if (gift.type == GiftType.SENT) friend.sent + 1 else friend.sent - 1,
                        )
                    )
                        .onFailure {
                            Log.d("UpdateGiftUseCase", "친구 정보 업데이트 실패")
                            Result.failure<Unit>(it)
                        }
                }
            }
    }
}