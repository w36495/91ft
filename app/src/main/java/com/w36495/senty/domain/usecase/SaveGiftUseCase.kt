package com.w36495.senty.domain.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.EditImage
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class SaveGiftUseCase @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImageRepository,
    private val friendRepository: FriendRepository,
    private val uploadGiftImageUseCase: UploadGiftImageUseCase,
) {
    private val mutex = Mutex()

    suspend operator fun invoke(gift: Gift, imageMap: Map<String, EditImage>): Result<Unit> {
        val result = mutex.withLock {
            giftRepository.insertGift(gift)
        }

        return result.fold(
                onSuccess = { giftId ->
                    coroutineScope {
                        saveGiftImages(giftId, imageMap, gift)
                        updateFriends(gift)
                    }

                    Result.success(Unit)
                },
                onFailure = { Result.failure(it) }
            )
    }


    private suspend fun saveGiftImages(giftId: String, imageMap: Map<String, EditImage>, gift: Gift) = coroutineScope {
        if (gift.images.isEmpty()) return@coroutineScope

        val results = mutableListOf<Deferred<Result<String>>>()

        val (thumbnailName, thumbnail) = imageMap.entries.first()
        if (thumbnail is EditImage.New) {
            results += async(Dispatchers.IO) {
                giftImageRepository.insertGiftImageByBitmap(giftId, "thumbs_$thumbnailName", thumbnail.byteArray)
            }
        }

        uploadGiftImageUseCase(giftId, imageMap)

        results.awaitAll()
    }

    private suspend fun updateFriends(gift: Gift) = supervisorScope {
        gift.friends.map { friend ->
            launch(Dispatchers.IO) {
                val current = friendRepository.getFriend(friend.id).getOrThrow()
                val updated = current.copy(
                    received = if (gift.type == GiftType.RECEIVED) current.received + 1 else current.received,
                    sent = if (gift.type == GiftType.SENT) current.sent + 1 else current.sent
                )

                friendRepository.patchFriend(updated)
                    .onSuccess { Timber.d("🟢 친구[${friend.id}] 정보 수정 완료") }
            }
        }.joinAll()
    }
}