package com.w36495.senty.data.manager.galleryimage.folder

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.w36495.senty.data.manager.galleryimage.entity.GalleryFolderEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GalleryFolderProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val allGalleryFolders = mutableListOf<GalleryFolderEntity>()

    suspend fun loadAllGalleryFolders(): Result<List<GalleryFolderEntity>> = withContext(Dispatchers.IO) {
        return@withContext try {
            allGalleryFolders.clear()

            val folders = LinkedHashMap<String, GalleryFolderEntity>()
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            context.contentResolver.query(
                uri,
                projection,
                null,
                null,
                SORT_ORDER,
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)


                while (cursor.moveToNext()) {
                    val bucketName = cursor.getString(bucketColumn) ?: "Unknown"
                    if (!folders.containsKey(bucketName)) {
                        val id = cursor.getLong(idColumn)
                        val uriForThumb = ContentUris.withAppendedId(uri, id)

                        folders[bucketName] = GalleryFolderEntity(
                            name = bucketName,
                            thumbnailUri = uriForThumb,
                            count = 1
                        )
                    } else {
                        // count 증가
                        folders[bucketName] = folders[bucketName]!!.copy(
                            count = folders[bucketName]!!.count + 1
                        )
                    }
                }
            }

            // "최근 항목" 폴더 추가 (전체 합)
            val allCount = folders.values.sumOf { it.count }
            val allFolder = GalleryFolderEntity(
                name = "all",
                thumbnailUri = folders.values.first().thumbnailUri,
                count = allCount
            )

            Result.success(listOf(allFolder) + folders.values)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val SORT_ORDER = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    }
}