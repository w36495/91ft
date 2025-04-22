package com.w36495.senty.view.screen.friend.list.contact

import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel

sealed interface FriendContact {
    sealed interface State {
        data object Idle : State
        data object Loading : State
        data class Success(
            val friends: List<FriendUiModel>,
            val friendGroups: List<FriendGroupUiModel>,
        ) : State
    }

    sealed interface Event {
        data object OnClickFriendAdd : Event
        data object OnClickFriendGroups : Event
        data class OnClickFriendDetail(val friendId: String) : Event
        data class OnSelectFriendGroup(val friendGroup: FriendGroupUiModel?) : Event
    }

    sealed interface Effect {
        data class ShowSnackBar(val message: String) : Effect
        data class NavigateToFriendDetail(val friendId: String) : Effect
        data object NavigateToFriendGroups : Effect
        data object NavigateToFriendAdd : Effect
    }
}