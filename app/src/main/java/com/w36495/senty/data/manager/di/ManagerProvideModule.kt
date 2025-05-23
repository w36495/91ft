package com.w36495.senty.data.manager.di

import android.content.Context
import com.w36495.senty.data.manager.galleryimage.GalleryImageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerProvideModule {
    @Provides
    @Singleton
    fun provideGalleryImageManager(
        @ApplicationContext context: Context
    ): GalleryImageManager {
        return GalleryImageManager(context)
    }
}