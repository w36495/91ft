package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteGiftUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImgRepository,
) {
    suspend operator fun invoke(gift: Gift, images: List<String>): Result<Unit> {
        return try {
            giftRepository.deleteGift(gift.id)
                .onSuccess {
                    val friend = friendRepository.getFriend(gift.friendId).getOrThrow()

                    val results = coroutineScope {
                        val deleteImageJobs = images.map { image ->
                            val parseImagePath = image
                                .substringAfterLast("/") // 전체 경로에서 마지막 segment 추출
                                .substringBefore("?") // 쿼리 제거
                                .substringAfterLast("%2F") // Firebase Storage 경로 추출
                            async { giftImageRepository.deleteGiftImage(gift.id, parseImagePath) }
                        }

                        val updateFriendJob = async {
                            friendRepository.patchFriend(
                                friend = friend.copy(
                                    received = if (gift.type == GiftType.RECEIVED) friend.received - 1 else friend.received,
                                    sent = if (gift.type == GiftType.SENT) friend.sent - 1 else friend.sent
                                )
                            )
                        }

                        deleteImageJobs + updateFriendJob
                    }.awaitAll()

                    results.forEach {
                        it.onFailure {
                            Log.d("DeleteGiftUseCase", it.stackTraceToString())
                        }
                    }

                    Result.success(Unit)
                }
                .onFailure {
                    Log.d("DeleteGiftUseCase", it.stackTraceToString())
                }
        } catch (e: Exception) {
            Log.d("DeleteGiftUseCase", e.stackTraceToString())
            Result.failure(e)
        }
    }
}