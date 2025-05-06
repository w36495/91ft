package com.w36495.senty.data.manager.di

import com.w36495.senty.data.manager.VersionInfoManagerImpl
import com.w36495.senty.domain.manager.VersionInfoManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {
    @Binds
    @Singleton
    abstract fun bindVersionInfoManager(
        versionInfoManagerImpl: VersionInfoManagerImpl
    ): VersionInfoManager
}