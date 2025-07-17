package com.w36495.senty.domain.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.local.datastore.DataStoreContact
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class UpdateGiftUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val friendRepository: FriendRepository,
    private val updateFriendUseCase: UpdateFriendUseCase,
    private val friendSyncFlagDataStore: DataStoreContact<Boolean>,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(gift: Gift): Result<Unit> {
        val originalGift = giftRepository.getGift(gift.id).getOrThrow()
        val isDifferentGiftType = originalGift.type != gift.type

        val deletedFriendIds = originalGift.friends.map { it.id }
            .filterNot { friendId -> gift.friends.map { it.id }.contains(friendId) }
        val addedFriendIds = gift.friends.map { it.id }
            .filterNot { friendId -> originalGift.friends.map { it.id }.contains(friendId) }

        return mutex
            .withLock { giftRepository.updateGift(gift) }
            .onSuccess {
                // 삭제된 친구 정보 업데이트
                if (deletedFriendIds.isNotEmpty()) {
                    coroutineScope {
                        deletedFriendIds.map { friendId ->
                            async {
                                val friend = friendRepository.getFriend(friendId).getOrThrow()
                                val receivedCount =
                                    if (gift.type == GiftType.RECEIVED) friend.received - 1 else friend.received
                                val sentCount =
                                    if (gift.type == GiftType.SENT) friend.sent - 1 else friend.sent

                                updateFriendUseCase(
                                    friend = friend.copy(
                                        received = receivedCount,
                                        sent = sentCount,
                                    )
                                )
                            }
                        }
                    }.awaitAll()
                }

                // 추가된 친구 정보 업데이트
                if (addedFriendIds.isNotEmpty()) {
                    coroutineScope {
                        addedFriendIds.map { friendId ->
                            async {
                                val friend = friendRepository.getFriend(friendId).getOrThrow()
                                val receivedCount = if (gift.type == GiftType.RECEIVED) friend.received + 1 else friend.received
                                val sentCount = if (gift.type == GiftType.SENT) friend.sent + 1 else friend.sent

                                updateFriendUseCase(
                                    friend = friend.copy(
                                        received = receivedCount,
                                        sent = sentCount,
                                    )
                                )
                            }
                        }
                    }.awaitAll()
                }

                if (deletedFriendIds.isEmpty() && addedFriendIds.isEmpty()) {
                    // 친구 정보 업데이트
                    if (isDifferentGiftType) {
                        coroutineScope {
                            gift.friends.map { friendId ->
                                async {
                                    val friend = friendRepository.getFriend(gift.friendId).getOrThrow()

                                    val receivedCount = if (gift.type == GiftType.RECEIVED) friend.received + 1 else friend.received - 1
                                    val sentCount = if (gift.type == GiftType.SENT) friend.sent + 1 else friend.sent - 1

                                    if (receivedCount < 0 || sentCount < 0) {
                                        friendSyncFlagDataStore.save(true)
                                    }

                                    updateFriendUseCase(
                                        friend.copy(
                                            received = receivedCount,
                                            sent = sentCount,
                                        )
                                    )
                                        .onFailure {
                                            Timber.d("🔴 친구 정보 업데이트 실패")
                                            friendSyncFlagDataStore.save(true)
                                        }
                                }
                            }
                        }.awaitAll()
                    }
                }
            }
    }
}