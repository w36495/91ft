package com.w36495.senty.di

import com.w36495.senty.data.manager.KakaoAuthManager
import com.w36495.senty.data.manager.SnsAuthManager
import com.w36495.senty.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AuthManagerModule {
    @Provides
    @Named("kakao")
    fun provideKakaoAuthManager(
        userRepository: UserRepository
    ): SnsAuthManager {
        return KakaoAuthManager(userRepository)
    }
}