package com.w36495.senty.domain.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.local.datastore.DataStoreContact
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
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

    suspend operator fun invoke(gift: Gift): Result<Unit> = mutex.withLock {
        runCatching {
            val originalGift = giftRepository.getGift(gift.id).getOrThrow()
            val isGiftTypeChanged = originalGift.type != gift.type

            val originalFriendIds = originalGift.friends.map { it.id }.toSet()
            val newFriendIds = gift.friends.map { it.id }.toSet()

            val addedFriendIds = newFriendIds - originalFriendIds
            val deletedFriendIds = originalFriendIds - newFriendIds
            val commonFriendIds = originalFriendIds intersect newFriendIds

            giftRepository.updateGift(gift).getOrThrow()

            coroutineScope {
                val jobs = mutableListOf<Deferred<Unit>>()

                if (isGiftTypeChanged) {
                    jobs += async { updateCountOnTypeChange(commonFriendIds, gift.type) }
                }

                if (addedFriendIds.isNotEmpty()) {
                    jobs += async { incrementFriendCount(addedFriendIds, gift.type) }
                }

                if (deletedFriendIds.isNotEmpty()) {
                    jobs += async { decrementFriendCount(deletedFriendIds, originalGift.type) }
                }

                jobs.awaitAll()
            }

            Unit
        }
    }

    private suspend fun decrementFriendCount(friendIds: Set<String>, giftType: GiftType) = coroutineScope {
        friendIds.map { friendId ->
            launch {
                val friend = friendRepository.getFriend(friendId).getOrThrow()
                val updatedFriend = when (giftType) {
                    GiftType.RECEIVED -> friend.copy(received = (friend.received - 1).coerceAtLeast(0))
                    GiftType.SENT -> friend.copy(sent = (friend.sent - 1).coerceAtLeast(0))
                }

                updateFriendUseCase(updatedFriend)
                    .onFailure {
                        Timber.d("🔴 친구 카운트 감소 실패")
                        friendSyncFlagDataStore.save(true)
                    }
            }
        }
    }.joinAll()

    private suspend fun incrementFriendCount(friendIds: Set<String>, giftType: GiftType) = coroutineScope {
        friendIds.map { friendId ->
            launch {
                val friend = friendRepository.getFriend(friendId).getOrThrow()

                val updatedFriend = when (giftType) {
                    GiftType.RECEIVED -> friend.copy(received = friend.received + 1)
                    GiftType.SENT -> friend.copy(sent = friend.sent + 1)
                }
                updateFriendUseCase(updatedFriend).getOrThrow()
            }
        }
    }.joinAll()

    private suspend fun updateCountOnTypeChange(friendIds: Set<String>, newGiftType: GiftType) = coroutineScope {
        friendIds.map { friendId ->
            launch {
                val friend = friendRepository.getFriend(friendId = friendId).getOrThrow()

                val updatedFriend = when (newGiftType) {
                    GiftType.RECEIVED -> friend.copy(
                        received = friend.received + 1,
                        sent = (friend.sent - 1).coerceAtLeast(0)
                    )

                    GiftType.SENT -> friend.copy(
                        sent = friend.sent + 1,
                        received = (friend.received - 1).coerceAtLeast(0)
                    )
                }

                val countsAreValid = updatedFriend.received >= 0 && updatedFriend.sent >= 0
                if (!countsAreValid) {
                    friendSyncFlagDataStore.save(true)
                }

                updateFriendUseCase(updatedFriend)
                    .onFailure {
                        Timber.d("🔴 친구 선물 카운트 변경 실패")
                        friendSyncFlagDataStore.save(true)
                    }
            }
        }
    }.joinAll()
}