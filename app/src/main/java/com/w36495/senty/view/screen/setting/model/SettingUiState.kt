package com.w36495.senty.view.screen.setting.model

sealed interface SettingContact {
    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Effect {
        data class ShowError(val throwable: Throwable? = null) : Effect
        data class ShowToast(val message: String) : Effect
        data object Complete : Effect
    }
}