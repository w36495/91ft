package com.w36495.senty.view.component.image.picker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.manager.galleryimage.GalleryImageManager
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.view.component.image.picker.model.GalleryFolderUiModel
import com.w36495.senty.view.component.image.picker.model.ImagePickerContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    private val galleryImageManager: GalleryImageManager,
) : ViewModel() {
    private val _effect = Channel<ImagePickerContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(ImagePickerContract.State())
    val state get() = _state.asStateFlow()

    init {
        loadImages()
    }

    fun selectImage(uri: Uri) {
        _state.update { it.copy(selectedImageUris = it.selectedImageUris + uri) }
    }

    fun unselectImage(index: Int) {
        _state.update {
            it.copy(
                selectedImageUris = it.selectedImageUris.filterIndexed { i, _ -> i != index }
            )
        }
    }

    fun addInitializedPage(page: Int) {
        _state.update { it.copy(initializedPages = it.initializedPages.filterIndexed { index, _ -> index != page }.toSet()) }
    }

    fun updateViewportSize(size: IntSize) {
        _state.update { it.copy(viewportSize = size) }
    }

    fun sendEffect(effect: ImagePickerContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }

    fun croppedImage(
        context: Context,
        scrollValues: List<Int>,
    ) {
        viewModelScope.launch {
            if (state.value.viewportSize == IntSize.Zero) return@launch

            val baseState = state.value

            val results = baseState.selectedImageUris
                .mapIndexed { index, uri ->
                    async {
                        cropAndSaveImage(
                            context = context,
                            uri = uri,
                            scrollValue = scrollValues[index],
                            viewportSize = state.value.viewportSize.width,
                        )
                    }
                }

            val resultUris = results.awaitAll()

            _state.update { it.copy(editedImageUris = resultUris) }
            _effect.send(ImagePickerContract.Effect.NavigateToImagePreview(state.value.editedImageUris))
        }
    }

    private suspend fun cropAndSaveImage(
        context: Context,
        uri: Uri,
        scrollValue: Int,
        viewportSize: Int,
    ): Uri = withContext(Dispatchers.IO) {
        // 1) URI → Bitmap
        val bitmap = ImageConverter.uriToBitmap(context, uri)

        // 이미지의 높이 Offset (startY, endY)
        val (startY, endY) = getVisibleImageRegion(scrollValue, bitmap.height, viewportSize)

        // 2) 크롭
        val cropped = withContext(Dispatchers.Default) {
            createCropImage(bitmap, startY, endY)
        }

        // 3) Bitmap → 파일 URI
        ImageConverter.bitmapToFileUri(context, cropped)
    }

    private fun getVisibleImageRegion(
        scrollOffset: Int,
        imageHeight: Int,
        viewportSize: Int,
    ): Pair<Int, Int> {
        val startY = scrollOffset.coerceIn(0, imageHeight)
        val endY = (scrollOffset + viewportSize).coerceIn(0, imageHeight)

        return startY to endY
    }

    private fun createCropImage(
        bitmap: Bitmap,
        imageStartY: Int,
        imageEndY: Int,
    ): Bitmap {
        val width = bitmap.width
        val height = (imageEndY - imageStartY).coerceAtLeast(1)
        return Bitmap.createBitmap(bitmap, 0, imageStartY, width, height)
    }

    private fun loadImages() {
        viewModelScope.launch {
            galleryImageManager.loadAllImages()

            val folders = galleryImageManager.getGalleryFolders().map {
                GalleryFolderUiModel(
                    name = it.name,
                    thumbnailUri = it.thumbnailUri,
                    count = it.count
                )
            }

            val images = galleryImageManager.getAllImages()
            _state.update {
                it.copy(
                    images = images,
                    galleryFolders = folders,
                    selectedImageUris = if (state.value.selectedImageUris.isEmpty()) {
                        it.selectedImageUris + images.first()
                    } else {
                        it.selectedImageUris
                    }
                )
            }
        }
    }
}