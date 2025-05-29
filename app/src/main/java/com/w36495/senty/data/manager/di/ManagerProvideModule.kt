package com.w36495.senty.data.manager.di

import android.content.Context
import com.w36495.senty.data.manager.galleryimage.GalleryImageProvider
import com.w36495.senty.data.manager.galleryimage.folder.GalleryFolderProvider
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
    fun provideGalleryImageProvider(
        @ApplicationContext context: Context
    ): GalleryImageProvider {
        return GalleryImageProvider(context)
    }

    @Provides
    @Singleton
    fun provideGalleryFolderProvider(
        @ApplicationContext context: Context,
    ): GalleryFolderProvider {
        return GalleryFolderProvider(context)
    }
}