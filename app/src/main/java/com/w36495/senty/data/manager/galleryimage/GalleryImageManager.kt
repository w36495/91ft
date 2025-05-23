package com.w36495.senty.data.manager.galleryimage

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.w36495.senty.data.manager.galleryimage.entity.GalleryFolderEntity
import com.w36495.senty.data.manager.galleryimage.entity.GalleryImageEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GalleryImageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val allImages = mutableListOf<GalleryImageEntity>()

    suspend fun loadAllImages() = withContext(Dispatchers.IO) {
        allImages.clear()

        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        context.contentResolver.query(uri, projection, null, null, SORT_ODER_DATE_ADDED)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val folder = cursor.getString(bucketColumn) ?: "Unknown"
                val imageUri = ContentUris.withAppendedId(uri, id)

                allImages.add(GalleryImageEntity(id = id, folderName = folder, uri = imageUri))
            }
        }
    }

    fun getGalleryFolders(): List<GalleryFolderEntity> {
        return listOf(
            GalleryFolderEntity(
                name = FOLDER_ALL,
                thumbnailUri = allImages.first().uri,
                count = allImages.size
            )
        ) + allImages
            .groupBy { it.folderName }
            .map { (folderName, images) ->
                GalleryFolderEntity(
                    name = folderName,
                    thumbnailUri = images.first().uri,
                    count = images.size,
                )
            }
    }

    fun getImagesInFolder(folderName: String): List<Uri> {
        return if (folderName == FOLDER_ALL) {
            allImages.map { it.uri }
        } else {
            allImages.filter { it.folderName == folderName }.map { it.uri }
        }
    }

    fun getAllImages(): List<Uri> = allImages.map { it.uri }

    companion object {
        private const val FOLDER_ALL = "최근 항목"
        private const val SORT_ODER_DATE_ADDED = "${MediaStore.Images.Media.DATE_ADDED} DESC"
    }
}