package com.w36495.senty.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.ImgConverter
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class GiftAddViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    private val giftCategoryRepository: GiftCategoryRepository,
    private val friendRepository: FriendRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg = _snackbarMsg.asSharedFlow()
    private var _gift = MutableStateFlow(Gift.emptyGift)
    val gift = _gift.asStateFlow()

    fun getGift(giftId: String) {
        viewModelScope.launch {
            giftRepository.getGift(giftId)
                .map { giftDetail ->
                    var imgPath = ""

                    if (giftDetail.imgUri.isNotEmpty()) {
                        coroutineScope {
                            val img = async { giftImgRepository.getGiftImages(giftDetail.id, giftDetail.imgUri) }

                            imgPath = img.await()
                        }
                    }

                    Gift(
                        giftDetail = giftDetail,
                        giftImg = imgPath
                    )
                }
                .collectLatest { gift ->
                    combine(friendRepository.getFriend(gift.giftDetail.friend.id),
                        giftCategoryRepository.getCategory(gift.giftDetail.category.id)) {
                            friend, category ->

                        gift.copy(
                            giftDetail = gift.giftDetail.copy(
                                friend = friend,
                                category = category
                            ),
                            giftImg = gift.giftImg
                        )
                    }.collectLatest { gift ->
                        Log.d("GiftAddVM", gift.toString())
                        _gift.update { gift }
                    }
                }
        }
    }

    fun updateGift(giftDetail: GiftDetail, giftImg: Any?) {
        viewModelScope.launch {
            val result = giftRepository.patchGift(giftDetail.id, giftDetail.toDataEntity())

            if (result.isSuccessful) {
                if (giftImg != "") {
                    if (!giftImg.toString().contains(giftDetail.imgUri)) {
                        result.body()?.let {
                            val giftImgName = saveGiftImg(giftDetail.id, giftImg)
                            val finalResult = giftRepository.patchGiftImgUri(giftDetail.id, giftImgName)

                            if (finalResult.isSuccessful) {
                                _snackbarMsg.emit("성공적으로 수정되었습니다")
                            } else {
                                _snackbarMsg.emit("수정에 실패하였습니다.")
                            }
                        }
                    }
                }
            }
        }
    }

    fun saveGift(gift: GiftDetail, giftImg: Any?) {
        viewModelScope.launch {
            val result = giftRepository.insertGift(gift.toDataEntity())

            if (result.isSuccessful) {
                result.body()?.let {
                    val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                    val key = jsonObject["name"].toString().replace("\"", "")

                    val giftImgName = async { saveGiftImg(key, giftImg) }
                    giftRepository.patchGiftImgUri(key, giftImgName.await())
                }
            } else {
                Log.d("GiftAddVM", "saveGift(Failed) : ${result.errorBody().toString()}")
            }
        }
    }

    fun validateGift(gift: GiftDetail): Boolean {
        var isValid = true

        if (gift.category == GiftCategory.emptyCategory) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("카테고리를 선택해주세요")
            }
        } else if (gift.friend.name.trim().isNullOrEmpty()) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("친구를 입력해주세요")
            }
        } else if (gift.date.isNullOrEmpty()) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("날짜를 입력해주세요")
            }
        }

        return isValid
    }

    private suspend fun saveGiftImg(giftId: String, giftImg: Any?): String = when (giftImg) {
        is Bitmap -> {
            val byteGiftImg = ImgConverter.bitmapToString(giftImg)
            requestGiftImg(giftId, byteGiftImg)
        }

        is Uri -> {
            var bitmap: Bitmap? = null

            bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.contentResolver, giftImg)
                )
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, giftImg)
            }

            bitmap?.let {
                requestGiftImg(giftId, ImgConverter.bitmapToString(it))
            } ?: ""
        }

        else -> ""
    }

    private suspend fun requestGiftImg(giftId: String, bitmap: String): String {
        return suspendCoroutine<String> { continuation ->
            viewModelScope.launch {

                val uri = giftImgRepository.insertGiftImgByBitmap(giftId, bitmap)
                continuation.resume(uri)
            }
        }
    }
}