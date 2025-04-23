package com.w36495.senty.view.screen.gift.detail.contact

import com.w36495.senty.view.screen.gift.model.GiftUiModel

sealed interface GiftDetailContact {
    data class State(
        val isLoading: Boolean = false,
        val gift: GiftUiModel = GiftUiModel(),
        val showDeleteDialog: Boolean = false,
    )

    sealed interface Event {
        data object OnClickBack : Event
        data class OnClickEdit(val giftId: String) : Event
        data object OnClickDelete : Event
        data class OnSelectDelete(val giftId: String?) : Event
    }

    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val message: String) : Effect
        data object NavigateToBack : Effect
        data class NavigateToEditGift(val giftId: String) : Effect
    }
}