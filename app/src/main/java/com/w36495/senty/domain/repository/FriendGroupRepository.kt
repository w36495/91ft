package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.FriendGroupEntity
import kotlinx.coroutines.flow.Flow

interface FriendGroupRepository {
    fun getFriendGroups(): Flow<List<FriendGroupEntity>>
}