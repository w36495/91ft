package com.w36495.senty.data.manager.galleryimage

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.w36495.senty.data.manager.galleryimage.entity.GalleryImageEntity

class GalleryImagePagingSource(
    private val contentResolver: ContentResolver,
    private val folderName: String?,
) : PagingSource<Int, GalleryImageEntity>() {
    override fun getRefreshKey(state: PagingState<Int, GalleryImageEntity>): Int? {
        return state.anchorPosition?.let { anchor ->
            val anchorPage = state.closestPageToPosition(anchor)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, GalleryImageEntity> {
        val page = params.key ?: 0
        val data = getGalleryImages(page, PAGE_SIZE, folderName)

        return try {
            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (data.size < PAGE_SIZE) null else page + 1

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            Log.d("GalleryImagePagingSource", e.stackTraceToString())
            LoadResult.Error(e)
        }
    }

    private fun getGalleryImages(
        page: Int,
        pageSize: Int,
        folderName: String? = null,
    ): List<GalleryImageEntity> {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.ORIENTATION,
        )

        val selection = folderName?.let { "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?" }
        val selectionArgs = folderName?.let { arrayOf(it) }
        val result = mutableListOf<GalleryImageEntity>()

        contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            SORT_ORDER,
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val orientationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.ORIENTATION)

            val offset = page * pageSize
            if (cursor.moveToPosition(offset)) {
                var count = 0

                do {
                    val id = cursor.getLong(idColumn)
                    val bucket = cursor.getString(bucketColumn) ?: "Unknown"
                    val imageUri = ContentUris.withAppendedId(uri, id)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val orientation = cursor.getInt(orientationColumn)

                    result.add(GalleryImageEntity(id, bucket, imageUri, width, height, orientation))
                    count++
                } while (cursor.moveToNext() && count < pageSize)
            }
        }

        return result
    }

    companion object {
        private const val PAGE_SIZE = 30
        private const val SORT_ORDER = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    }
}