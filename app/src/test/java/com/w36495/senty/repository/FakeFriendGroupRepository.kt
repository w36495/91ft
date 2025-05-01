package com.w36495.senty.repository

import android.util.Log
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.entity.FriendGroup
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeFriendGroupRepository : FriendGroupRepository {
    private val _friendGroups = MutableStateFlow<List<FriendGroup>>(emptyList())
    override val friendGroups: StateFlow<List<FriendGroup>>
        get() = _friendGroups.asStateFlow()

    override suspend fun getFriendGroup(friendGroupId: String): Result<FriendGroup> {
        return try {
            val friend = friendGroups.value.first { it.id == friendGroupId }
            Result.success(friend)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFriendGroups(): Result<Unit> {
        return try {
            _friendGroups.update {
                List(10) {
                    FriendGroupUiModel(id = "Group $it").toDomain()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("FakeFriendGroupRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertFriendGroup(newFriendGroup: FriendGroup): Result<Unit> {
        return try {
            _friendGroups.update {
                it + newFriendGroup
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun patchFriendGroup(friendGroup: FriendGroup): Result<Unit> {
        return try {
            _friendGroups.update {
                it.map { existing ->
                    if (existing.id == friendGroup.id) friendGroup else existing
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFriendGroup(friendGroupKey: String): Result<Unit> {
        return try {
            _friendGroups.update { friends ->
                friends.filterNot { it.id == friendGroupKey }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("FriendRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}