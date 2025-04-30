package com.w36495.senty.view.screen.anniversary.contact

import com.w36495.senty.view.screen.anniversary.model.ScheduleUiModel
import org.threeten.bp.LocalDate

sealed interface AnniversaryBottomSheetContact {
    data class State(
        val isLoading: Boolean = false,
        val schedule: ScheduleUiModel = ScheduleUiModel(),
        val selectedDate: Triple<Int, Int, Int> = Triple(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth),
        val isErrorTitle: Boolean = false,
        val showCalendar: Boolean = false,
        val showTimePicker: Boolean = false,
        val showDeleteDialog: Boolean = false,
    )

    sealed interface Event {
        data object OnClickSave : Event
        data object OnClickUpdate : Event
        data object OnClickDelete : Event
        data object OnClickClose : Event
        data class UpdateDate(val selectedDate: Triple<Int, Int, Int>) : Event
        data class UpdateTitle(val title: String) : Event
        data class UpdateLocation(val location: String) : Event
        data class UpdateTime(val time: String) : Event
        data object ResetTime : Event
        data class UpdateMemo(val memo: String) : Event
        data object ShowCalendar : Event
        data object HideCalendar : Event
        data object ShowTimePicker : Event
        data object HideTimePicker : Event
        data object ShowDeleteDialog : Event
        data object HideDeleteDialog : Event
    }

    sealed interface Effect {
        data class ShowError(val throwable: Throwable?) : Effect
        data class ShowToast(val message: String) : Effect
    }
}