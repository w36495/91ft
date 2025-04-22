package com.w36495.senty.view.screen.gift.edit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.ImgConverter
import com.w36495.senty.view.screen.gift.edit.contact.EditGiftContact
import com.w36495.senty.view.screen.gift.edit.model.ImageSelectionType
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditGiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _effect = Channel<EditGiftContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(EditGiftContact.State())
    val state get() = _state.asStateFlow()

    fun handleEvent(event: EditGiftContact.Event) {
        when (event) {
            EditGiftContact.Event.OnClickBack -> {
                sendEffect(EditGiftContact.Effect.NavigateToBack)
            }
            EditGiftContact.Event.OnClickSave -> {
                if (!validateInputForm()) return

                saveGift(state.value.gift, state.value.images)
            }
            EditGiftContact.Event.OnClickEdit -> {

            }
            EditGiftContact.Event.OnClickImageAdd -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(showImageSelectionDialog = true)
                    }
                }
            }
            EditGiftContact.Event.OnClickFriend -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(showFriendsDialog = true)
                    }
                }
            }
            EditGiftContact.Event.OnClickFriendAdd -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(showFriendsDialog = false)
                    }
                }
                sendEffect(EditGiftContact.Effect.NavigateToFriendAdd)
            }
            EditGiftContact.Event.OnClickGiftCategory -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(showGiftCategoriesDialog = true)
                    }
                }
            }
            EditGiftContact.Event.OnClickGiftCategoriesEdit -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(showGiftCategoriesDialog = false)
                    }
                }
                sendEffect(EditGiftContact.Effect.NavigateToGiftCategories)
            }
            EditGiftContact.Event.OnClickDate -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(showDatePickerDialog = true)
                    }
                }
            }
            is EditGiftContact.Event.OnSelectImageSelectionType -> {
                event.type?.let {
                    when(it) {
                        ImageSelectionType.CAMERA -> {
                            sendEffect(EditGiftContact.Effect.ShowCamera)
                            viewModelScope.launch {
                                _state.update { state ->
                                    state.copy(showImageSelectionDialog = false)
                                }
                            }
                        }
                        ImageSelectionType.GALLERY -> {
                            sendEffect(EditGiftContact.Effect.ShowGallery)
                            viewModelScope.launch {
                                _state.update { state ->
                                    state.copy(showImageSelectionDialog = false)
                                }
                            }
                        }
                    }
                } ?: run {
                    // 닫기
                    viewModelScope.launch {
                        _state.update { state ->
                            state.copy(showImageSelectionDialog = false)
                        }
                    }
                }
            }
            is EditGiftContact.Event.OnSelectGiftType -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(
                            gift = state.gift.copy(type = event.type)
                        )
                    }
                }
            }
            is EditGiftContact.Event.OnSelectFriend -> {
                viewModelScope.launch {
                    event.friend?.let { friend ->
                        _state.update { state ->
                            state.copy(
                                showFriendsDialog = false,
                                isErrorFriend = false,
                                gift = state.gift.copy(
                                    friendId = friend.id,
                                    friendName = friend.name,
                                )
                            )
                        }
                    } ?: run {
                        _state.update { state -> state.copy(showFriendsDialog = false) }
                    }
                }
            }
            is EditGiftContact.Event.OnSelectGiftCategory -> {
                viewModelScope.launch {
                    event.category?.let { category ->
                        _state.update { state ->
                            state.copy(
                                showGiftCategoriesDialog = false,
                                isErrorGiftCategory = false,
                                gift = state.gift.copy(
                                    categoryId = category.id,
                                    categoryName = category.name,
                                )
                            )
                        }
                    } ?: run {
                        _state.update { state -> state.copy(showGiftCategoriesDialog = false) }
                    }
                }
            }
            is EditGiftContact.Event.OnSelectDate -> {
                viewModelScope.launch {
                    event.date?.let { date ->
                        _state.update { state ->
                            state.copy(
                                showDatePickerDialog = false,
                                isErrorDate = false,
                                gift = state.gift.copy(
                                    date = date
                                )
                            )
                        }
                    } ?: run {
                        _state.update { state -> state.copy(showDatePickerDialog = false) }
                    }
                }
            }
            is EditGiftContact.Event.UpdateMood -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(
                            gift = state.gift.copy(mood = event.mood)
                        )
                    }
                }
            }
            is EditGiftContact.Event.UpdateMemo -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(
                            gift = state.gift.copy(memo = event.memo)
                        )
                    }
                }
            }
            is EditGiftContact.Event.UpdateImage -> {
                setGiftImg(event.image)
            }
            is EditGiftContact.Event.RemoveImage -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(
                            gift = state.gift.copy(hasImages = state.images.isNotEmpty()),
                            images = state.images.minus(state.images[event.index]).toList(),
                        )
                    }
                }
            }
        }
    }

    fun getGift(giftId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val giftResult = giftRepository.getGift(giftId)
            val imgResult = giftImgRepository.getGiftImages(giftId)

            giftResult.onSuccess { gift ->
                _state.update { it.copy(gift = gift.toUiModel()) }
            }

            imgResult
                .onSuccess { imgPaths ->
                    val sortedPaths = imgPaths.sortedBy {
                        it.substringAfterLast("%2F").substringBefore("?")
                    }

                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            images = sortedPaths,
                            originalImages = imgPaths,
                        )
                    }
                }
                .onFailure {
                    Log.d("EditGiftVM", it.stackTraceToString())
                    _state.update { state ->
                        state.copy(isLoading = false)
                    }

                    sendEffect(EditGiftContact.Effect.ShowError("오류가 발생하였습니다."))
                }
        }
    }

    fun sendEffect(effect: EditGiftContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun setGiftImg(img: Any) {
        if (img is Uri) {
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(context.contentResolver, img)
                )
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, img)
            }

            val formattedGiftImg = ImgConverter.bitmapToByteArray(bitmap)
            viewModelScope.launch {
                _state.update { state ->
                    state.copy(
                        gift = state.gift.copy(hasImages = true),
                        images = state.images.plus(formattedGiftImg).toList()
                    )
                }
            }
        } else if (img is Bitmap) {
            val formattedGiftImg = ImgConverter.bitmapToByteArray(img)

            viewModelScope.launch {
                _state.update { state ->
                    state.copy(
                        gift = state.gift.copy(hasImages = true),
                        images = state.images.plus(formattedGiftImg).toList()
                    )
                }
            }
        }
    }

    private fun validateInputForm(): Boolean {
        val currentGift = _state.value.gift

        val friendError = currentGift.friendName.isEmpty()
        val categoryError = currentGift.categoryName.isEmpty()
        val dateError = currentGift.date.isEmpty()

        _state.update { state ->
            state.copy(
                isErrorFriend = friendError,
                isErrorGiftCategory = categoryError,
                isErrorDate = dateError,
            )
        }

        return !(friendError || categoryError || dateError)
    }



//    fun updateGift(giftDetail: GiftDetail) {
//        viewModelScope.launch {
//            val result = giftRepository.patchGift(giftDetail.id, giftDetail.toDataEntity())
//
//            if (result.isSuccessful) {
//                if (giftImages.value.isNotEmpty()) {
//                    giftImages.value.forEach { giftImage ->
//                        if (giftImage is ByteArray) {
//                            giftImgRepository.insertGiftImgByBitmap(giftDetail.id, giftImage)
//                        }
//                    }
//                }
//
//                coroutineScope {
//                    originalGiftImages.forEach { originalImg ->
//                        if (!giftImages.value.contains(originalImg)) {
//                            val parsePath = originalImg.split("/").run {
//                                this[lastIndex].split("?")[0]
//                            }.split("%2F")
//
//                            val imageResult = async {
//                                giftImgRepository.deleteGiftImg(
//                                    giftDetail.id,
//                                    parsePath[parsePath.lastIndex]
//                                )
//                            }.await()
//
//                            if (!imageResult) {
//                                _snackbarMsg.emit("이미지 삭제 중 오류가 발생하였습니다.")
//                            }
//                        }
//                    }
//                }
//
//                isSaved.value = true
//            }
//        }
//    }

    private fun saveGift(gift: GiftUiModel, images: List<Any>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val giftInsertResult = giftRepository.insertGift(gift.toDomain())
            giftInsertResult
                .onSuccess { giftId ->
                    if (images.isEmpty()) {
                        _state.update {
                            it.copy(isLoading = false)
                        }
                        setGiftImg(EditGiftContact.Effect.ShowToast("등록 완료되었습니다."))
                    } else {
                        images.filterIsInstance<ByteArray>().map { image ->
                            giftImgRepository.insertGiftImageByBitmap(giftId, image)
                                .onFailure {
                                    _state.update { it.copy(isLoading = false) }
                                    Log.d("EditGiftVM", "이미지 저장 실패 : ${it.stackTraceToString()}")
                                    sendEffect(EditGiftContact.Effect.ShowError("선물 등록 중 오류가 발생하였습니다."))
                                }
                        }

                        _state.update {
                            it.copy(isLoading = false)
                        }
                        sendEffect(EditGiftContact.Effect.ShowToast("등록 완료되었습니다."))
                    }
                }
                .onFailure {
                    Log.d("EditGiftMV", "선물 등록 실패 : ${it.stackTraceToString()}")
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(EditGiftContact.Effect.ShowError("선물 등록에 실패하였습니다."))
                }
        }
    }
}