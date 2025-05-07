package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.local.datastore.DataStoreContact
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class UpdateGiftUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val friendRepository: FriendRepository,
    private val updateFriendUseCase: UpdateFriendUseCase,
    private val friendSyncFlagDataStore: DataStoreContact<Boolean>,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(gift: Gift): Result<Unit> {
        val beforeGift = giftRepository.getGift(gift.id).getOrThrow()
        val isDifferentGiftType = beforeGift.type != gift.type

        return mutex
            .withLock { giftRepository.updateGift(gift) }
            .onSuccess {
                // 친구 정보 업데이트
                if (isDifferentGiftType) {
                    val friend = friendRepository.getFriend(gift.friendId).getOrThrow()

                    val receivedCount = if (gift.type == GiftType.RECEIVED) friend.received + 1 else friend.received - 1
                    val sentCount = if (gift.type == GiftType.SENT) friend.sent + 1 else friend.sent - 1

                    if (receivedCount < 0 || sentCount < 0) {
                        friendSyncFlagDataStore.save(true)
                        return Result.failure(Exception("친구 정보 업데이트 실패"))
                    }

                    updateFriendUseCase(
                        friend.copy(
                            received = receivedCount,
                            sent = sentCount,
                        )
                    )
                        .onFailure {
                            Log.d("UpdateGiftUseCase", "친구 정보 업데이트 실패")
                            friendSyncFlagDataStore.save(true)
                            return Result.failure(it)
                        }
                }
            }
    }
}