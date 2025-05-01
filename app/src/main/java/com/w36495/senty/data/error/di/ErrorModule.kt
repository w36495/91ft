package com.w36495.senty.data.error.di

import com.w36495.senty.data.error.ErrorHandlerImpl
import com.w36495.senty.data.error.ErrorMessageProviderImpl
import com.w36495.senty.domain.error.ErrorHandler
import com.w36495.senty.domain.error.ErrorMessageProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModule {
    @Binds
    @Singleton
    abstract fun bindErrorHandler(errorHandlerImpl: ErrorHandlerImpl): ErrorHandler

    @Binds
    @Singleton
    abstract fun bindErrorMessageProvider(
        errorMessageProviderImpl: ErrorMessageProviderImpl
    ): ErrorMessageProvider
}