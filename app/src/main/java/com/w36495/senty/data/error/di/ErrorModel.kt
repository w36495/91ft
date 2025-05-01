package com.w36495.senty.data.error.di

import com.w36495.senty.data.error.ErrorHandler
import com.w36495.senty.data.error.ErrorHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorModel {
    @Binds
    @Singleton
    abstract fun bindErrorHandler(errorHandlerImpl: ErrorHandlerImpl): ErrorHandler
}