package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.FriendGroup
import kotlinx.coroutines.flow.StateFlow

interface FriendGroupRepository {
    val friendGroups: StateFlow<List<FriendGroup>>

    suspend fun getFriendGroup(friendGroupId: String): Result<FriendGroup>
    suspend fun getFriendGroups(): Result<Unit>
    suspend fun insertFriendGroup(newFriendGroup: FriendGroup): Result<Unit>
    suspend fun patchFriendGroup(friendGroup: FriendGroup): Result<Unit>
    suspend fun deleteFriendGroup(friendGroupKey: String): Result<Unit>
}