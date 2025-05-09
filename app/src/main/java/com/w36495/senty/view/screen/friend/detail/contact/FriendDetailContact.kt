package com.w36495.senty.view.screen.friend.detail.contact

import com.w36495.senty.view.screen.friend.detail.model.FriendDetailGiftUiModel
import com.w36495.senty.view.screen.friend.model.FriendUiModel

sealed interface FriendDetailContact {
    data class State(
        val isLoading: Boolean = false,
        val friend: FriendUiModel = FriendUiModel(),
        val gifts: List<FriendDetailGiftUiModel> = emptyList(),
        val showDeleteDialog: Boolean = false,
    )

    sealed interface Event {
        data object OnClickBack : Event
        data class OnClickGift(val giftId: String) : Event
        data class OnClickEdit(val friendId: String) : Event
        data object OnClickDelete : Event
        data class OnSelectDelete(val friendId: String?) : Event

    }

    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val throwable: Throwable? = null) : Effect
        data object NavigateToFriends : Effect
        data class NavigateToEditFriend(val friendId: String) : Effect
        data class NavigateToGiftDetail(val giftId: String) : Effect
    }
}