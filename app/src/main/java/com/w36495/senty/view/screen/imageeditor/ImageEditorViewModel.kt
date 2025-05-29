package com.w36495.senty.view.screen.imageeditor

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.view.screen.imageeditor.model.ImageEditorContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageEditorViewModel @Inject constructor(

) : ViewModel() {
    private val _effect = Channel<ImageEditorContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(ImageEditorContact.State())
    val state get() = _state.asStateFlow()

    fun handleEvent(event: ImageEditorContact.Event) {
        when (event) {
            ImageEditorContact.Event.OnClickFlipVertical -> {
                _state.update { it.copy(isFlippedVertical = !it.isFlippedVertical) }
            }
            ImageEditorContact.Event.OnClickFlipHorizontal -> {
                _state.update { it.copy(isFlippedHorizontal = !it.isFlippedHorizontal) }
            }

            ImageEditorContact.Event.ShowSaveDialog -> {
                _state.update { it.copy(showSaveDialog = true) }
            }
            ImageEditorContact.Event.OnClickSave -> {
                _state.update {
                    it.copy(
                        isLoading = true,
                        isEditing = false,
                        showSaveDialog = false,
                    )
                }
            }
            ImageEditorContact.Event.OnDismissSaveDialog -> {
                _state.update { it.copy(showSaveDialog = false) }
            }
            ImageEditorContact.Event.OnClickReset -> {

            }
            ImageEditorContact.Event.OnClickPrev -> {
                _state.update {
                    it.copy(currentEditIndex = it.currentEditIndex - 1)
                }
            }
            ImageEditorContact.Event.OnClickNext -> {
                if (state.value.isEditing) {
                    _state.update { it.copy(showSaveDialog = true) }
                } else {
                    _state.update {
                        it.copy(
                            isEditing = true,
                            currentEditIndex = it.currentEditIndex + 1,
                            isFlippedHorizontal = false,
                            isFlippedVertical = false,
                        )
                    }
                }
            }
            is ImageEditorContact.Event.OnClickComplete -> {
                // 저장된 상태가 아니라면
                // 저장 다이얼로그 호출
                // 저장된 상태라면
                // 이미지 편집 프리뷰로 이동
                val editedFileUris =state.value.editedImages.map {
                    ImageConverter.bitmapToFileUri(event.context, it)
                }.map { it.toString() }

                sendEffect(ImageEditorContact.Effect.NavigateToImageEditorPreview(editedFileUris))
            }
            is ImageEditorContact.Event.UpdateEditImageSpec -> {
                Log.d("ImageEditorViewModel", "UpdateEditImageSpec: $event")
                _state.update { state ->
                    state.copy(
                        editImageState = state.editImageState.copy(
                            offset = event.offset,
                            size = event.size,
                        )
                    )
                }
            }
            is ImageEditorContact.Event.UpdateEditImage -> {
                // 이미 저장된 이미지라면 바꿔치기
                if (state.value.editedImages.contains(event.image)) {
                    val index = state.value.editedImages.indexOf(event.image)
                    _state.update { state ->
                        state.copy(
                            editedImages = state.editedImages.mapIndexed { i, image ->
                                if (i == index) event.image else image
                            }
                        )
                    }
                } else {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            isEditing = state.currentEditIndex != state.originalImageUris.lastIndex,
                            editedImages = state.editedImages + event.image,
                        )
                    }
                }

                sendEffect(ImageEditorContact.Effect.ShowSnackBar("저장되었습니다."))
            }
            is ImageEditorContact.Event.OnClickSaveDialog -> {
                if (event.isComplete) {
                    _state.update {
                        it.copy(
                            showSaveDialog = false,
                            isEditing = false,
                        )
                    }
                } else {
                    _state.update { it.copy(showSaveDialog = false) }
                }
            }
        }
    }

    fun setImageUris(uris: List<Uri>) {
        _state.update {
            it.copy(
                isLoading = false,
                originalImageUris = uris
            )
        }
    }

    fun sendEffect(effect: ImageEditorContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}