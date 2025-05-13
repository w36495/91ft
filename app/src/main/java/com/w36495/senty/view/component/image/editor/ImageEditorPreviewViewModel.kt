package com.w36495.senty.view.component.image.editor

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ImageEditorPreviewViewModel @Inject constructor(

) : ViewModel() {
    private val _editedImages = MutableStateFlow<List<Bitmap>>(emptyList())
    val editedImages get() = _editedImages.asStateFlow()

    fun addEditedImage(image: Bitmap) {
        Log.d("ImageEditorPreviewVM", "편집 완료된 이미지 : $image")
        _editedImages.update {
            it + image
        }
    }
}