package com.w36495.senty.di

import com.w36495.senty.data.repository.FriendGroupRepositoryImpl
import com.w36495.senty.domain.FriendGroupRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindFriendGroupRepository(
        friendGroupRepositoryImpl: FriendGroupRepositoryImpl
    ): FriendGroupRepository
}