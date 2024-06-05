package com.w36495.senty.viewModel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.ImgConverter
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
class GiftAddViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImgRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    fun updateGift(giftDetail: GiftDetailEntity, giftImg: Any?) {
        viewModelScope.launch {
            val result = giftRepository.patchGift(giftDetail.toDataEntity())

            if (result.isSuccessful) {
                if (giftImg != "") {
                    result.body()?.let {
                        val giftImgName = saveGiftImg(giftDetail.id, giftImg)
                        giftRepository.patchGiftImgUri(giftDetail.id, giftImgName)
                    }
                }
            }
        }
    }

    fun saveGift(gift: GiftDetailEntity, giftImg: Any?) {
        viewModelScope.launch {
            val result = giftRepository.insertGift(gift.toDataEntity())

            if (result.isSuccessful) {
                result.body()?.let {
                    val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                    val key = jsonObject["name"].toString().replace("\"", "")

                    val giftKeyResult = giftRepository.patchGiftKey(key)
                    if (giftKeyResult.isSuccessful) {
                        if (giftKeyResult.body()?.string() == it.string()) {
                        }
                    }
                    val giftImgName = async { saveGiftImg(key, giftImg) }
                    giftRepository.patchGiftImgUri(key, giftImgName.await())

                    saveGiftImg(key, giftImg, onSuccess = { imgName ->
                        launch(giftImgJob) {
                            giftRepository.patchGiftImgUri(key, imgName)
                        }
                    })

                    giftImgJob.join()
                }
            } else {
                Log.d("GiftAddVM", "saveGift(Failed) : ${result.errorBody().toString()}")
            }
        }
    }

    private fun saveGiftImg(giftId: String, giftImg: Any?, onSuccess: (String) -> Unit) {
        when (giftImg) {
            is Bitmap -> {
                val byteGiftImg = giftImg.toByteArray()
                giftImageRepository.insertGiftImgByBitmap(giftId, byteGiftImg,
                    onSuccess = { onSuccess(it) }
                )
            }

            is Uri -> {
                giftImageRepository.insertGiftImgByUri(giftId, giftImg,
                    onSuccess = { onSuccess(it) }
                )
            }
        }
    }
}