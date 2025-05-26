package com.w36495.senty.data.manager.galleryimage

import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GalleryImageProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getGalleryImages(folderName: String? = null): Flow<PagingData<Uri>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            pagingSourceFactory = {
                GalleryImagePagingSource(context.contentResolver, folderName)
            }
        )
            .flow
            .map { pagingData -> pagingData.map { it.uri } }
    }
}