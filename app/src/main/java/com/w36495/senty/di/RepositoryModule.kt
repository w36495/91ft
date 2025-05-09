package com.w36495.senty.di

import com.w36495.senty.data.repository.AnniversaryRepositoryImpl
import com.w36495.senty.data.repository.AuthRepositoryImpl
import com.w36495.senty.data.repository.FriendGroupRepositoryImpl
import com.w36495.senty.data.repository.FriendRepositoryImpl
import com.w36495.senty.data.repository.GiftCategoryRepositoryImpl
import com.w36495.senty.data.repository.GiftImageRepositoryImpl
import com.w36495.senty.data.repository.GiftRepositoryImpl
import com.w36495.senty.data.repository.MapSearchRepositoryImpl
import com.w36495.senty.data.repository.UserRepositoryImpl
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.domain.repository.AuthRepository
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.repository.MapSearchRepository
import com.w36495.senty.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFriendGroupRepository(
        friendGroupRepositoryImpl: FriendGroupRepositoryImpl
    ): FriendGroupRepository

    @Binds
    @Singleton
    abstract fun bindFriendRepository(
        friendRepositoryImpl: FriendRepositoryImpl
    ): FriendRepository

    @Binds
    @Singleton
    abstract fun bindGiftRepository(
        giftRepositoryImpl: GiftRepositoryImpl
    ): GiftRepository

    @Binds
    @Singleton
    abstract fun bindGiftCategoryRepository(
        giftCategoryRepositoryImpl: GiftCategoryRepositoryImpl
    ): GiftCategoryRepository

    @Binds
    @Singleton
    abstract fun bindGiftImageRepository(
        GiftImageRepositoryImpl: GiftImageRepositoryImpl
    ): GiftImageRepository

    @Binds
    @Singleton
    abstract fun bindAnniversaryRepository(
        anniversaryRepositoryImpl: AnniversaryRepositoryImpl
    ): AnniversaryRepository

    @Binds
    @Singleton
    @NetworkModule.naver
    abstract fun bindMapSearchRepository(
        mapSearchRepositoryImpl: MapSearchRepositoryImpl
    ): MapSearchRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

}