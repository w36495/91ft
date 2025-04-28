package com.w36495.senty.view.screen.gift.edit.contact

import android.net.Uri
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.gift.edit.model.EditGiftUiModel
import com.w36495.senty.view.screen.gift.edit.model.ImageSelectionType

sealed interface EditGiftContact {
    data class State(
        val isLoading: Boolean = false,
        val gift: EditGiftUiModel = EditGiftUiModel(),
        val isErrorGiftCategory: Boolean = false,
        val isErrorFriend: Boolean = false,
        val isErrorDate: Boolean = false,
        val showDatePickerDialog: Boolean = false,
        val showFriendsDialog: Boolean = false,
        val showImageSelectionDialog: Boolean = false,
        val showGiftCategoriesDialog: Boolean = false,
    )

    sealed interface Event {
        data object OnClickSave : Event
        data object OnClickEdit : Event
        data object OnClickBack : Event
        data object OnClickImageAdd : Event
        data object OnClickFriend : Event
        data object OnClickFriendAdd : Event
        data object OnClickDate : Event
        data object OnClickGiftCategory : Event
        data object OnClickGiftCategoriesEdit : Event
        data class OnSelectImageSelectionType(val type: ImageSelectionType?) : Event
        data class OnSelectGiftType(val type: GiftType) : Event
        data class OnSelectFriend(val friend: FriendUiModel?) : Event
        data class OnSelectGiftCategory(val category: GiftCategoryUiModel?) : Event
        data class OnSelectDate(val date: String?) : Event
        data class UpdateMood(val mood: String) : Event
        data class UpdateMemo(val memo: String) : Event
        data class UpdateImage(val image: Uri) : Event
        data class RemoveImage(val imageName: String) : Event
    }

    sealed interface Effect {
        data class ShowToast(val message: String) : Effect
        data class ShowError(val message: String) : Effect
        data object ShowCamera : Effect
        data object ShowGallery : Effect
        data object NavigateToBack : Effect
        data object NavigateToFriendAdd : Effect
        data object NavigateToGiftCategories : Effect
    }
}