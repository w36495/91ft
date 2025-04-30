package com.w36495.senty.view.screen.gift.category.contact

import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel

sealed interface GiftCategoryContact {
    data class State(
        val isLoading: Boolean = false,
        val categories: List<GiftCategoryUiModel> = emptyList(),
        val selectedCategory: GiftCategoryUiModel? = null,
        val showAddCategoryDialog: Boolean = false,
        val showEditCategoryDialog: Boolean = false,
        val showDeleteCategoryDialog: Boolean = false,
    )

    sealed interface Event {
        data class OnClickAdd(val category: GiftCategoryUiModel?) : Event
        data object OnClickBack : Event
        data class OnClickEdit(val category: GiftCategoryUiModel?) : Event
        data class OnClickDelete(val category: GiftCategoryUiModel?) : Event
        data object OnSelectDelete : Event
        data class OnSelectEdit(val category: GiftCategoryUiModel) : Event
    }

    sealed interface Effect {
        data class ShowToast(val message: String): Effect
        data class ShowError(val message: String) : Effect
        data object NavigateToSettings : Effect
    }
}