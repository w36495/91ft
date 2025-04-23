package com.w36495.senty.view.screen.gift.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.screen.gift.detail.contact.GiftDetailContact
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
) : ViewModel() {
    private val _effect = Channel<GiftDetailContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(GiftDetailContact.State())
    val state = _state.asStateFlow()

    fun handleEvent(event: GiftDetailContact.Event) {
        when (event) {
            GiftDetailContact.Event.OnClickBack -> {
                sendEffect(GiftDetailContact.Effect.NavigateToBack)
            }
            GiftDetailContact.Event.OnClickDelete -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(showDeleteDialog = true)
                    }
                }
            }
            is GiftDetailContact.Event.OnClickEdit -> {
                sendEffect(GiftDetailContact.Effect.NavigateToEditGift(event.giftId))
            }
            is GiftDetailContact.Event.OnSelectDelete -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(showDeleteDialog = false)
                    }

                    event.giftId?.let { removeGift(state.value.gift) }
                }
            }
        }
    }

    fun sendEffect(effect: GiftDetailContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
    fun getGift(giftId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = giftRepository.getGift(giftId)

            result
                .onSuccess {
                    val gift = it.toUiModel()

                    val enrichedGifts = giftImgRepository.getGiftImages(giftId)
                        .map { images -> gift.copy(images = images) }
                        .getOrElse { gift }

                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            gift = enrichedGifts,
                        )
                    }
                }
                .onFailure {
                    Log.d("GiftDetailVM", "선물 정보 조회 실패")
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(GiftDetailContact.Effect.ShowError("오류가 발생하였습니다."))
                }
        }
    }

    private fun removeGift(gift: GiftUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = giftRepository.deleteGift(gift.id)

            result
                .onSuccess {
                    giftImgRepository.deleteAllGiftImage(gift.id)
                        .onSuccess { Log.d("GiftDetailVM", "선물 이미지 삭제 성공") }
                        .onFailure {
                            Log.d("GiftDetailVM", "선물 이미지 삭제 실패 : ${it.stackTraceToString()}")
                        }

                    launch {
                        friendRepository.getFriend(gift.friendId)
                            .onSuccess {
                                friendRepository.patchFriend(
                                    it.copy(
                                        received = if (gift.type == GiftType.RECEIVED) it.received - 1 else it.received,
                                        sent = if (gift.type == GiftType.SENT) it.sent - 1 else it.sent
                                    )
                                )
                            }
                    }

                _state.update { it.copy(isLoading = false) }
                    sendEffect(GiftDetailContact.Effect.ShowToast("선물이 삭제되었습니다."))
            }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(GiftDetailContact.Effect.ShowError("오류가 발생하였습니다."))
                }
        }
    }
}