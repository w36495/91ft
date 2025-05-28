package com.w36495.senty.data.manager.galleryimage

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.w36495.senty.data.manager.galleryimage.entity.GalleryImageEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GalleryImageProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getGalleryImages(folderName: String? = null): Flow<PagingData<GalleryImageEntity>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            pagingSourceFactory = {
                GalleryImagePagingSource(context.contentResolver, folderName)
            }
        ).flow
    }
}