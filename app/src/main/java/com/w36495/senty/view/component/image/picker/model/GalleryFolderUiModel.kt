package com.w36495.senty.view.component.image.picker.model

import android.net.Uri

data class GalleryFolderUiModel(
    val name: String,
    val thumbnailUri: Uri,
    val count: Int,
) {
    val isAll = name == "all"

    fun getFolderNameKr(): String {
        return folderNameMap[name.lowercase()] ?: name
    }

    companion object {
        private val folderNameMap = mapOf(
            "all" to "최근 항목",
            "camera" to "카메라",
            "download" to "다운로드",
            "screenshots" to "스크린샷",
        )
    }
}