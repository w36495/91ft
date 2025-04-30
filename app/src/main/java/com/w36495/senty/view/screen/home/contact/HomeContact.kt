package com.w36495.senty.view.screen.home.contact

import com.w36495.senty.view.screen.home.model.HomeGiftUiModel
import com.w36495.senty.view.screen.home.model.HomeScheduleUiModel

sealed interface HomeContact {
    data class State(
        val isReceivedGiftLoading: Boolean = true,
        val isSentGiftLoading: Boolean = true,
        val isAnniversaryLoading: Boolean = true,
        val schedules: List<HomeScheduleUiModel> = emptyList(),
        val receivedGifts: List<HomeGiftUiModel> = emptyList(),
        val sentGifts: List<HomeGiftUiModel> = emptyList(),
    )

    sealed interface Event {
        data object OnClickGifts : Event
        data class OnClickGift(val giftId: String) : Event
    }

    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data object NavigateToGifts : Effect
        data class NavigateToGiftDetail(val giftId: String) : Effect
    }
}