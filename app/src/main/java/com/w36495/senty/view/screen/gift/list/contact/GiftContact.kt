package com.w36495.senty.view.screen.gift.list.contact

import com.w36495.senty.view.screen.gift.list.model.GiftListUiModel

sealed interface GiftContact {
    data class State(
        val isLoading: Boolean = true,
        val gifts: List<GiftListUiModel> = emptyList(),
    )

    sealed interface Event {
        data object OnClickBack : Event
        data object OnClickGiftCategories : Event
        data class OnClickGift(val giftId: String) : Event
        data class OnSelectTab(val tab: Int) : Event
    }

    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val throwable: Throwable?) : Effect
        data object NavigateToBack : Effect
        data object NavigateToGiftCategories : Effect
        data class NavigateToGiftDetail(val giftId: String) : Effect
    }
}