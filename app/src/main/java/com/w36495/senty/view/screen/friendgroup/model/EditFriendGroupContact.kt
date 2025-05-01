package com.w36495.senty.view.screen.friendgroup.model

sealed interface EditFriendGroupContact {
    sealed interface Effect {
        data class ShowError(val message: String) : Effect
        data class NavigateFriendGroups(val message: String) : Effect
    }

    sealed interface State {
        data object Idle : State
        data object Loading : State
        data object Success : State
    }

    sealed interface Event {
        data class onSaveFriendGroup(val friendGroup: FriendGroupUiModel) : Event
        data class onEditFriendGroup(val friendGroup: FriendGroupUiModel) : Event
    }
}