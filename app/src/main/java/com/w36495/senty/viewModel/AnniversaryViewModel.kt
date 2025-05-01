package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.screen.anniversary.contact.AnniversaryContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnniversaryViewModel @Inject constructor(
    private val anniversaryRepository: AnniversaryRepository
) : ViewModel() {
    private val _effect = Channel<AnniversaryContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(AnniversaryContact.State())
    val state get() = _state.asStateFlow()

    fun handleEvent(event: AnniversaryContact.Event) {
        when (event) {
            AnniversaryContact.Event.OnClickAddSchedule -> {
                _state.update { it.copy(showBottomSheet = true) }
            }
            AnniversaryContact.Event.OnClickCloseScheduleBottomSheet -> {
                _state.update {
                    it.copy(
                        selectedSchedule = null,
                        showBottomSheet = false,
                    )
                }
            }
            is AnniversaryContact.Event.OnClickSchedule -> {
                _state.update { state ->
                    state.copy(
                        selectedSchedule = event.schedule,
                        showBottomSheet = true,
                    )
                }
            }
            is AnniversaryContact.Event.UpdateSelectedDate -> {
                getSchedules(event.year, event.month, event.day)
            }
        }
    }

    init {
        observeSchedules()
    }

    private fun observeSchedules() {
        viewModelScope.launch {
            anniversaryRepository.fetchSchedules()

            val (selectedYear, selectedMonth, selectedDay) = _state.value.selectedDate

            anniversaryRepository.schedules
                .map { schedules ->
                    val allSchedule = schedules.map { it.toUiModel() }

                    val selectedSchedules = schedules.filter { schedule ->
                        val (year, month, day) = schedule.date.split("-").map { it.toInt() }
                        selectedYear == year
                                && StringUtils.format2Digits(selectedMonth).toInt() == month
                                && StringUtils.format2Digits(selectedDay).toInt() == day
                    }.map { it.toUiModel() }

                    allSchedule to selectedSchedules
                }
                .catch {
                    Log.d("AnniversaryVM", it.stackTraceToString())
                    _effect.send(AnniversaryContact.Effect.ShowError(it))
                }
                .collectLatest { (allSchedule, selectedSchedules) ->
                    _state.update { state ->
                        state.copy(
                            schedules = allSchedule,
                            selectedSchedules = selectedSchedules
                        )
                    }
                }
        }
    }

    private fun getSchedules(selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
        runCatching {
            val selectedSchedules = state.value.schedules.filter { schedule ->
                val (year, month, day) = schedule.date.split("-").map { it.toInt() }
                selectedYear == year
                        && StringUtils.format2Digits(selectedMonth).toInt() == month
                        && StringUtils.format2Digits(selectedDay).toInt() == day
            }

            _state.update {
                it.copy(
                    selectedSchedules = selectedSchedules,
                    selectedDate = Triple(selectedYear, selectedMonth, selectedDay),
                )
            }
        }.onFailure {
            viewModelScope.launch {
                _effect.send(AnniversaryContact.Effect.ShowError(it))
            }
        }
    }
}