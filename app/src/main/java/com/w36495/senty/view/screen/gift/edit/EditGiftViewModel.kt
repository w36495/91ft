package com.w36495.senty.view.screen.gift.edit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEditUiModel
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.util.toLinkedMap
import com.w36495.senty.view.screen.gift.edit.contact.EditGiftContact
import com.w36495.senty.view.screen.gift.edit.model.EditImage
import com.w36495.senty.view.screen.gift.edit.model.ImageSelectionType
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EditGiftViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImageRepository,
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

                saveGift()
            }
            EditGiftContact.Event.OnClickEdit -> {
                if (!validateInputForm()) return

                updateGift()
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
                uploadImage(event.image)
            }
            is EditGiftContact.Event.RemoveImage -> {
                viewModelScope.launch {
                    _state.update { state ->
                        state.copy(
                            gift = state.gift.copy(
                                images = state.gift.images
                                    .toMutableMap()
                                    .apply { remove(event.imageName) }
                                    .toLinkedMap()
                            ),
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

            giftResult
                .onSuccess { gift ->
                    // 이미지 가져오기
                    if (gift.images.isNotEmpty()) {
                        giftImageRepository.getGiftImages(giftId)
                            .onSuccess { imagePaths ->
                                val originalImagePaths = linkedMapOf<String, EditImage>().apply {
                                    imagePaths
                                        .map { path ->
                                            val key = path.substringAfterLast("%2F").substringBefore(".jpg?")
                                            key to EditImage.Original(path)
                                        }
                                        .sortedBy { (key, _) -> key } // 필요 시 정렬
                                        .forEach { (key, image) -> this[key] = image }
                                }

                                _state.update { state ->
                                    state.copy(
                                        isLoading = false,
                                        gift = gift
                                            .toEditUiModel()
                                            .copy(images = originalImagePaths)
                                    )
                                }
                            }
                    } else {
                        _state.update { state ->
                            state.copy(
                                isLoading = false,
                                gift = gift.toEditUiModel()
                            )
                        }
                    }
                }
                .onFailure {
                    Log.d("EditGiftVM", it.stackTraceToString())
                    _state.update { state -> state.copy(isLoading = false) }

                    sendEffect(EditGiftContact.Effect.ShowError(it))
                }
        }
    }

    fun sendEffect(effect: EditGiftContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun uploadImage(image: Uri) {
        val bitmap = ImageConverter.uriToBitmap(context, image)
        val resizedBitmap = ImageConverter.resizeToWidth(context, bitmap)

        val imageName = System.currentTimeMillis().toString()

        _state.update { state ->
            val updatedImages = state.gift.images.toMutableMap().apply {
                this[imageName] = EditImage.New(resizedBitmap)
            }

            state.copy(
                gift = state.gift.copy(images = LinkedHashMap(updatedImages)),
            )
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

    private fun updateGift() {
        if (state.value.isLoading) return

        viewModelScope.launch {
            Log.d("EditGiftVM","🟢 선물 수정 시작")
            _state.update { it.copy(isLoading = true) }

            val updateGift = state.value.gift

            val result = giftRepository.updateGift(updateGift.copy(
                thumbnail = updateGift.images.entries.firstOrNull()?.let {
                    if (it.value is EditImage.New) {
                        "thumbs_${it.key}"
                    } else {
                        // 기존 썸네일과 같다면
                        if (updateGift.thumbnail?.contains(it.key) == true) {
                            updateGift.thumbnail
                        } else {
                            "thumbs_${it.key}"
                        }
                    }
                }
            ).toDomain())

            result
                .onSuccess {
                    if (updateGift.images.isNotEmpty()) {
                        // 새로운 이미지 저장
                        coroutineScope {
                            val resultJobs = mutableListOf<Deferred<Result<Unit>>>()

                            val (firstImageName, firstImage) = updateGift.images.entries.first()

                            // 새로운 썸네일
                            if (firstImage is EditImage.New) {
                                resultJobs += async {
                                    val resizedThumbnail = ImageConverter.resizeToWidth(context, firstImage.bitmap, 600)
                                    val webPThumbnail = ImageConverter.compressToWebP(resizedThumbnail)

                                    giftImageRepository.insertGiftImageByBitmap(updateGift.id, "thumbs_$firstImageName", webPThumbnail)
                                }
                            } else {
                                val sameThumbnail = updateGift.thumbnail?.contains(firstImageName) == true

                                if (!sameThumbnail) {
                                    val bitmap = withContext(Dispatchers.IO) {
                                        ImageConverter.urlToBitmap((firstImage as EditImage.Original).path)
                                    }

                                    if (bitmap != null) {
                                        resultJobs += async {
                                            Log.d("EditGiftVM","🟢 새로운 썸네일 저장 시작")
                                            val newResizedThumbnail = ImageConverter.resizeToWidth(context, bitmap, 600)
                                            val newThumbnail = ImageConverter.compressToWebP(newResizedThumbnail)

                                            giftImageRepository.insertGiftImageByBitmap(updateGift.id, "thumbs_$firstImageName", newThumbnail)
                                        }

                                        resultJobs += async {
                                            Log.d("EditGiftVM","🟢 기존 썸네일 삭제 시작")
                                            giftImageRepository.deleteGiftImage(updateGift.id, "thumbs_${updateGift.originalImages.first()}")
                                        }
                                    }
                                }
                            }

                            // 새로운 이미지 저장
                            updateGift.images.map { (imageName, image) ->
                                if (image is EditImage.New) {
                                    resultJobs += async {
                                        val webPImage = ImageConverter.compressToWebP(image.bitmap)
                                        giftImageRepository.insertGiftImageByBitmap(updateGift.id, imageName, webPImage)
                                    }
                                }
                            }

                            // 삭제해야 할 이미지
                            val deleteImages = updateGift.originalImages.filterNot { it in updateGift.images.keys.toList() }

                            deleteImages.map { deleteImageName ->
                                resultJobs += async {
                                    giftImageRepository.deleteGiftImage(updateGift.id, deleteImageName)
                                }
                            }

                            resultJobs.awaitAll()
                        }
                    } else {
                        // 기존 이미지를 모두 삭제햇다면
                        if (updateGift.originalImages.isNotEmpty()) {
                            giftImageRepository.deleteAllGiftImage(updateGift.id)
                        }
                    }

                    giftRepository.fetchGifts()
                    Log.d("EditGiftVM","🟢 선물 수정 완료")
                    _state.update { state -> state.copy(isLoading = false) }
                    sendEffect(EditGiftContact.Effect.ShowToast("수정이 완료되었습니다."))
                }
                .onFailure {
                    Log.d("EditGiftVM", "선물 수정 실패 : ${it.stackTraceToString()}")
                    _state.update { state -> state.copy(isLoading = false) }
                    sendEffect(EditGiftContact.Effect.ShowError(it))
                }
        }
    }

    private fun saveGift() {
        if (state.value.isLoading) return

        viewModelScope.launch {
            Log.d("EditGiftVM","🟢 선물 저장 시작")
            _state.update { it.copy(isLoading = true) }

            val gift = state.value.gift
            val result = giftRepository.insertGift(gift.copy(
                thumbnail = if (gift.images.entries.firstOrNull() != null) {
                    "thumbs_${gift.images.entries.first().key}"
                } else null
            ).toDomain())

            result
                .onSuccess { giftId ->
                    if (gift.images.isNotEmpty()) {
                        coroutineScope {
                            val resultJobs = mutableListOf<Deferred<Result<Unit>>>()
                            // 썸네일 저장
                            val (thumbnailName, thumbnailBitmap) = gift.images.entries.first()
                            if (thumbnailBitmap is EditImage.New) {
                                resultJobs += async {
                                    val resizedThumbnail = ImageConverter.resizeToWidth(context, thumbnailBitmap.bitmap, 600)
                                    val webPThumbnail = ImageConverter.compressToWebP(resizedThumbnail)

                                    giftImageRepository.insertGiftImageByBitmap(giftId, "thumbs_$thumbnailName", webPThumbnail)
                                }
                            }

                            // 이미지 저장
                            resultJobs += gift.images.mapNotNull { (imageName, image) ->
                                if (image is EditImage.New) {
                                    async {
                                        val webPImage = ImageConverter.compressToWebP(image.bitmap)
                                        giftImageRepository.insertGiftImageByBitmap(giftId, imageName, webPImage)
                                    }
                                } else null
                            }

                            resultJobs.awaitAll()
                        }
                    }

                    _state.update {
                        it.copy(isLoading = false)
                    }
                    Log.d("EditGiftVM","🟢 선물 저장 완료")

                    sendEffect(EditGiftContact.Effect.ShowToast("등록 완료되었습니다."))

                    friendRepository.getFriend(state.value.gift.friendId)
                        .onSuccess {
                            Log.d("EditGiftVM","🟢 친구 정보 수정 시작")
                            friendRepository.patchFriend(
                                it.copy(
                                    received = if (state.value.gift.type == GiftType.RECEIVED) it.received + 1 else it.received,
                                    sent = if (state.value.gift.type == GiftType.SENT) it.sent + 1 else it.sent
                                )
                            ).onSuccess {
                                Log.d("EditGiftVM","🟢 친구 정보 수정 완료")
                            }
                        }
                }
                .onFailure {
                    Log.d("EditGiftVM","🔴 선물 저장 실패" )

                    _state.update { it.copy(isLoading = false) }
                    sendEffect(EditGiftContact.Effect.ShowError(it))
                }
        }
    }
}