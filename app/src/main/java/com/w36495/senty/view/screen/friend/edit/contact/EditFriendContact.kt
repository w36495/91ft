package com.w36495.senty.view.screen.friend.edit.contact

import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel

sealed interface EditFriendContact {
    data class State(
        val isLoading: Boolean = false,
        val friend: FriendUiModel = FriendUiModel(),
        val isErrorName: Boolean = false,
        val isErrorGroup: Boolean = false,
        val checkBirthdaySkipped: Boolean = false,
        val showCalendarDialog: Boolean = false,
        val showFriendGroupSelectionDialog: Boolean = false,
    )

    sealed interface Event {
        data object OnClickFriendGroups : Event
        data object OnClickSave : Event
        data object OnClickEdit : Event
        data object OnClickBack : Event
        data object OnClickCalendar : Event
        data object OnClickFriendGroupSelectionDialog : Event
        data class UpdateFriendName(val name: String) : Event
        data class UpdateFriendGroup(val group: FriendGroupUiModel) : Event
        data class UpdateFriendBirthday(val birthday: String, val checkBirthdaySkipped: Boolean) : Event
        data class UpdateFriendMemo(val memo: String) : Event
    }

    sealed interface Effect {
        data class ShowSnackBar(val message: String) : Effect
        data class ShowError(val message: String) : Effect
        data object NavigateToFriendGroups : Effect
        data object NavigateToFriends : Effect
    }
}