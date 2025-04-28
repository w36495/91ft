package com.w36495.senty.view.screen.setting.model

sealed interface SettingEffect {
    data class ShowToast(val message: String) : SettingEffect
    data object Complete : SettingEffect
}