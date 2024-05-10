package com.w36495.senty.domain

import com.w36495.senty.data.domain.FriendGroupEntity

interface FriendGroupRepository {
    suspend fun getFriendGroups(): List<FriendGroupEntity>
}