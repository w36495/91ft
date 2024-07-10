package com.w36495.senty.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.exception.NetworkConnectionException
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.ImgConverter
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
class GiftAddViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    private val giftCategoryRepository: GiftCategoryRepository,
    private val friendRepository: FriendRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val originalGiftImages = mutableListOf<String>()
    var isSaved = mutableStateOf(false)
        private set
    var giftImages = mutableStateOf<List<Any>>(emptyList())
        private set
    private val _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg = _snackbarMsg.asSharedFlow()
    private var _giftDetail = MutableStateFlow(GiftDetail.emptyGiftDetail)
    val giftDetail = _giftDetail.asStateFlow()

    fun setGiftImg(img: Any) {
        if (img is Uri) {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.contentResolver, img)
                )
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, img)
            }

            val formattedGiftImg = ImgConverter.bitmapToByteArray(bitmap)
            giftImages.value = giftImages.value.plus(formattedGiftImg).toList()
        } else if (img is Bitmap) {
            val formattedGiftImg = ImgConverter.bitmapToByteArray(img)
            giftImages.value = giftImages.value.plus(formattedGiftImg).toList()
        }
    }

    fun removeGiftImage(index: Int) {
        giftImages.value = giftImages.value.minus(giftImages.value[index]).toList()
    }

    fun getGift(giftId: String) {
        viewModelScope.launch {
            coroutineScope {
                val imgPath = async { giftImgRepository.getGiftImages(giftId) }.await()

                originalGiftImages.addAll(imgPath)

                val sortedGiftImg = imgPath.sortedBy {
                    it.split("/").run {
                        this[lastIndex].split("?")[0]
                    }.split("%2F")[1]
                }
                giftImages.value = sortedGiftImg.toList()
            }

            giftRepository.getGift(giftId)
                .collectLatest { giftDetail ->
                    combine(
                        friendRepository.getFriend(giftDetail.friend.id),
                        giftCategoryRepository.getCategory(giftDetail.category.id)
                    ) { friend, category ->

                        giftDetail.copy(
                            friend = friend,
                            category = category
                        )
                    }.collectLatest { giftDetail ->
                        _giftDetail.update { giftDetail }
                    }
                }
        }
    }

    fun updateGift(giftDetail: GiftDetail) {
        viewModelScope.launch {
            val result = giftRepository.patchGift(giftDetail.id, giftDetail.toDataEntity())

            if (result.isSuccessful) {
                if (giftImages.value.isNotEmpty()) {
                    giftImages.value.forEach { giftImage ->
                        if (giftImage is ByteArray) {
                            giftImgRepository.insertGiftImgByBitmap(giftDetail.id, giftImage)
                        }
                    }
                }

                coroutineScope {
                    originalGiftImages.forEach { originalImg ->
                        if (!giftImages.value.contains(originalImg)) {
                            val parsePath = originalImg.split("/").run {
                                this[lastIndex].split("?")[0]
                            }.split("%2F")

                            val imageResult = async {
                                giftImgRepository.deleteGiftImg(
                                    giftDetail.id,
                                    parsePath[parsePath.lastIndex]
                                )
                            }.await()

                            if (!imageResult) {
                                _snackbarMsg.emit("이미지 삭제 중 오류가 발생하였습니다.")
                            }
                        }
                    }
                }

                isSaved.value = true
            }
        }
    }

    fun saveGift(gift: GiftDetail) {
        viewModelScope.launch {
            try {
                val result = giftRepository.insertGift(gift.toDataEntity())

                if (result.isSuccessful) {
                    result.body()?.let {
                        val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                        val key = jsonObject["name"].toString().replace("\"", "")

                        if (giftImages.value.isNotEmpty()) {
                            giftImages.value.forEach { giftImage ->
                                if (giftImage is ByteArray) {
                                    giftImgRepository.insertGiftImgByBitmap(key, giftImage)
                                }
                            }
                        }
                    }
                    isSaved.value = true
                }
            } catch (networkConnectionException: NetworkConnectionException) {
                _snackbarMsg.emit(networkConnectionException.message)
            } catch (exception: Exception) {
                Log.d("GiftAddVM(exception)", exception.message.toString())
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
        } else if (gift.friend.name.trim().isEmpty()) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("친구를 입력해주세요")
            }
        } else if (gift.date.isEmpty()) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("날짜를 입력해주세요")
            }
        }

        return isValid
    }

    fun writeSnackMsg(msg: String) {
        viewModelScope.launch {
            _snackbarMsg.emit(msg)
        }
    }
}