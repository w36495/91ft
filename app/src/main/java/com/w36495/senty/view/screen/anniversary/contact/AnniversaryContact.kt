package com.w36495.senty.view.screen.anniversary.contact

import com.w36495.senty.view.screen.anniversary.model.ScheduleUiModel
import org.threeten.bp.LocalDate

sealed interface AnniversaryContact {
    data class State(
        val selectedSchedule: ScheduleUiModel? = null,
        val schedules: List<ScheduleUiModel> = emptyList(),
        val selectedSchedules: List<ScheduleUiModel> = emptyList(),
        val selectedDate: Triple<Int, Int, Int> = Triple(LocalDate.now().year, LocalDate.now().monthValue, LocalDate.now().dayOfMonth),
        val showBottomSheet: Boolean = false,
    )

    sealed interface Event {
        data class OnClickSchedule(val schedule: ScheduleUiModel) : Event
        data object OnClickAddSchedule : Event
        data object OnClickCloseScheduleBottomSheet : Event
        data class UpdateSelectedDate(val year: Int, val month: Int, val day: Int) : Event
    }

    sealed interface Effect {
        data class ShowError(val throwable: Throwable?) : Effect
    }
}