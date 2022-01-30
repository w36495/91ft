package com.w36495.senty.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ImagePermission {

    fun getImageByGallery(view: View, resultGalleryImage: ActivityResultLauncher<Intent>) {
        val writePermission = ContextCompat.checkSelfPermission(view.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(view.context, Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(view.context as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), 888)
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = MediaStore.Images.Media.CONTENT_TYPE
            resultGalleryImage.launch(galleryIntent)
        }
    }

    // TODO 카메라로부터 사진 가져오기
    fun getImageByCamera(view: View) {
        val permission = ContextCompat.checkSelfPermission(view.context, Manifest.permission.CAMERA)
        if (permission == PackageManager.PERMISSION_DENIED) {
            // 권한이 없어서 요청
            ActivityCompat.requestPermissions(view.context as Activity, arrayOf(Manifest.permission.CAMERA), 777)
        }
    }

}