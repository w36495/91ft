package com.w36495.senty.di

import com.w36495.senty.data.repository.AccountRepositoryImpl
import com.w36495.senty.data.repository.AnniversaryRepositoryImpl
import com.w36495.senty.data.repository.FriendGroupRepositoryImpl
import com.w36495.senty.data.repository.FriendRepositoryImpl
import com.w36495.senty.data.repository.GiftCategoryRepositoryImpl
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.data.repository.GiftImgRepositoryImpl
import com.w36495.senty.data.repository.GiftRepositoryImpl
import com.w36495.senty.data.repository.MapSearchRepositoryImpl
import com.w36495.senty.data.repository.ProfileRepositoryImpl
import com.w36495.senty.domain.repository.AccountRepository
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.repository.MapSearchRepository
import com.w36495.senty.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindFriendGroupRepository(
        friendGroupRepositoryImpl: FriendGroupRepositoryImpl
    ): FriendGroupRepository

    @Binds
    abstract fun bindFriendRepository(
        friendRepositoryImpl: FriendRepositoryImpl
    ): FriendRepository

    @Binds
    abstract fun bindGiftRepository(
        giftRepositoryImpl: GiftRepositoryImpl
    ): GiftRepository

    @Binds
    abstract fun bindGiftCategoryRepository(
        giftCategoryRepositoryImpl: GiftCategoryRepositoryImpl
    ): GiftCategoryRepository

    @Binds
    @Singleton
    abstract fun bindGiftImgRepository(
        giftImgRepositoryImpl: GiftImgRepositoryImpl
    ): GiftImgRepository

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
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}