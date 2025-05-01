package com.w36495.senty.domain.usecase

import android.util.Log
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DeleteFriendGroupUseCase @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
    private val deleteFriendUseCase: DeleteFriendUseCase,
) {
    suspend operator fun invoke(friendGroupId: String): Result<Unit> {
        return friendGroupRepository.deleteFriendGroup(friendGroupId)
            .onSuccess {
                val friends = friendRepository.getFriendsByFriendGroup(friendGroupId).getOrElse { emptyList() }

                if (friends.isNotEmpty()) {
                    val results = coroutineScope {
                        friends.map { friend ->
                            async {
                                deleteFriendUseCase(friend)
                            }
                        }
                    }.awaitAll()

                    results.forEach { result ->
                        result.onFailure {
                            Log.d("DeleteFriendGroupUseCase", it.stackTraceToString())
                        }
                    }
                }

                Result.success(Unit)
            }
            .onFailure {
                Result.failure<Unit>(it)
            }
    }
}