package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.EditImage
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
                    if (gift.images.isNotEmpty()) {
                        coroutineScope {
                            val resultJobs = mutableListOf<Deferred<Result<String>>>()
                            // 썸네일 저장
                            val (thumbnailName, thumbnail) = imageMap.entries.first()
                            if (thumbnail is EditImage.New) {
                                resultJobs += async {
                                    giftImageRepository.insertGiftImageByBitmap(giftId, "thumbs_$thumbnailName", thumbnail.byteArray)
                                }
                            }

                            // 이미지 저장
                            uploadGiftImageUseCase(giftId, imageMap)

                            resultJobs.awaitAll()
                        }
                    }

                    friendRepository.getFriend(gift.friendId)
                        .onSuccess {
                            Log.d("EditGiftVM","🟢 친구 정보 수정 시작")
                            friendRepository.patchFriend(
                                it.copy(
                                    received = if (gift.type == GiftType.RECEIVED) it.received + 1 else it.received,
                                    sent = if (gift.type == GiftType.SENT) it.sent + 1 else it.sent
                                )
                            ).onSuccess {
                                Log.d("EditGiftVM","🟢 친구 정보 수정 완료")
                            }
                        }
                    Result.success(Unit)
                },
                onFailure = {
                    Result.failure<Unit>(it)
                }
            )
    }
}