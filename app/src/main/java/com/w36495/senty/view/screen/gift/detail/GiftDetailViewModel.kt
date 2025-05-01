package com.w36495.senty.view.screen.gift.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.usecase.DeleteGiftAndUpdateFriendUseCase
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
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImageRepository,
    private val deleteGiftAndUpdateFriendUseCase: DeleteGiftAndUpdateFriendUseCase,
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

                    val enrichedGifts = giftImageRepository.getGiftImages(giftId)
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
                    sendEffect(GiftDetailContact.Effect.ShowError(it))
                }
        }
    }

    private fun removeGift(gift: GiftUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            deleteGiftAndUpdateFriendUseCase(gift.toDomain())
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(GiftDetailContact.Effect.ShowToast("선물이 삭제되었습니다."))
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                    sendEffect(GiftDetailContact.Effect.ShowError(it))
                }
        }
    }
}