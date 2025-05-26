package com.w36495.senty.view.component.image.picker

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.common.primitives.Ints.min
import com.w36495.senty.data.manager.galleryimage.GalleryImageProvider
import com.w36495.senty.data.manager.galleryimage.folder.GalleryFolderProvider
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.error.GlobalError
import com.w36495.senty.domain.error.ImagePickerError
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.view.component.image.picker.model.GalleryFolderUiModel
import com.w36495.senty.view.component.image.picker.model.ImagePickerContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    private val galleryImageProvider: GalleryImageProvider,
    private val galleryFolderProvider: GalleryFolderProvider,
) : ViewModel() {
    private val _effect = Channel<ImagePickerContract.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(ImagePickerContract.State())
    val state get() = _state.asStateFlow()

    private val _selectedFolder = MutableStateFlow<GalleryFolderUiModel?>(null)
    val selectedFolder get() = _selectedFolder.asStateFlow()
    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingImages: Flow<PagingData<Uri>> = _selectedFolder
        .flatMapLatest { folder ->
            folder?.let {
                if (it.isAll) galleryImageProvider.getGalleryImages()
                else galleryImageProvider.getGalleryImages(it.name)
            } ?: run { galleryImageProvider.getGalleryImages() }
        }
        .cachedIn(viewModelScope)

    init {
        getAllGalleryFolders()
    }

    fun selectImage(uri: Uri) {
        _state.update { it.copy(selectedImageUris = it.selectedImageUris + uri) }
    }

    fun unselectImage(index: Int) {
        _state.update {
            it.copy(
                selectedImageUris = it.selectedImageUris.filterIndexed { i, _ -> i != index },
                initializedPages = it.initializedPages.filterIndexed { i, _ -> i != index }.toSet(),
            )
        }
    }

    fun addInitializedPage(page: Int) {
        _state.update { it.copy(initializedPages = it.initializedPages.filterIndexed { index, _ -> index != page }.toSet()) }
    }

    fun updateViewportSize(size: IntSize) {
        _state.update { it.copy(viewportSize = size) }
    }

    fun selectGalleryFolder(folder: GalleryFolderUiModel) {
        _selectedFolder.update { folder }
        _state.update {
            it.copy(showGalleryGroupBottomSheet = false,)
        }
    }

    fun clickGalleryFolder() {
        _state.update { it.copy(showGalleryGroupBottomSheet = !it.showGalleryGroupBottomSheet) }
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
        Log.d("ImagePickerVM", "cropAndSaveImage() : $startY, $endY")

        // 2) 크롭
        val cropped = withContext(Dispatchers.Default) {
            createCropImage(bitmap, startY, viewportSize)
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
        viewportSize: Int,
    ): Bitmap {
        val imageWidth = bitmap.width
        val imageHeight = bitmap.height

        val widthRatio = viewportSize.toFloat() / imageWidth
        val heightRatio = viewportSize.toFloat() / imageHeight

        // 현재 CROP 으로 보여주고 있으니 max를 통해 ratio 계산
        val scaleFactor = max(widthRatio, heightRatio)

        // 화면에 보여지는 이미지의 크기
        val displayImageWidth = imageWidth * scaleFactor

        // 사용자가 스크롤한 만큼 더해줌
        val cropTopInScaled = imageStartY
        val cropLeftInScaled = (displayImageWidth -  viewportSize) / 2

        val cropY = (cropTopInScaled / scaleFactor).toInt().coerceIn(0, bitmap.height - 1)
        val cropX = (cropLeftInScaled / scaleFactor).toInt().coerceIn(0, bitmap.width - 1)

        val cropSizeInBitmap = (viewportSize / scaleFactor).toInt()

        // 잘릴 범위가 원본 범위를 벗어나지 않도록 보정
        val finalCropWidth = min(cropSizeInBitmap, imageWidth - cropX)
        val finalCropHeight = min(cropSizeInBitmap, imageHeight - cropY)

        return Bitmap.createBitmap(bitmap, cropX, cropY, finalCropWidth, finalCropHeight)
    }

    private fun getAllGalleryFolders() {
        viewModelScope.launch {
            runCatching {
                galleryFolderProvider.loadAllGalleryFolders()
                    .onSuccess { allFolders ->
                        _selectedFolder.value = allFolders.first().toUiModel()
                        _state.update {
                            it.copy(
                                galleryFolders = allFolders.map {  folder -> folder.toUiModel() }
                            )
                        }
                    }
                    .onFailure {
                        Log.d("ImagePickerVM", "getAllGalleryFolder() : ${it.stackTraceToString()}")
                        sendEffect(ImagePickerContract.Effect.ShowError(ImagePickerError.NoGalleryFolders))
                    }
            }.onFailure {
                Log.d("ImagePickerVM", "getAllGalleryFolder() : ${it.stackTraceToString()}")
                sendEffect(ImagePickerContract.Effect.ShowError(GlobalError.UnKnownError))
            }
        }
    }
}