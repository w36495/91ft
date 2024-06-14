package com.w36495.senty.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.util.DateUtil
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.entity.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnniversaryViewModel @Inject constructor(
    private val anniversaryRepository: AnniversaryRepository
) : ViewModel() {
    private val _snackMsg = MutableSharedFlow<String>()
    val snackMsg = _snackMsg.asSharedFlow()

    private var selectDate = mutableStateOf(DateUtil.getDateTime())

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules = _schedules.asStateFlow()

    fun getSchedules(selectYear: Int, selectMonth: Int, selectDay: Int) {
        selectDate.value = "${selectYear}-${selectMonth}-${selectDay}"

        viewModelScope.launch {
            anniversaryRepository.getSchedules()
                .map {
                    it.filter { schedule ->
                        selectYear == schedule.getYear()
                                && StringUtils.format2Digits(selectMonth).toInt() == schedule.getMonth()
                                && StringUtils.format2Digits(selectDay).toInt() == schedule.getDay()
                    }
                }
                .collectLatest { schedules ->
                    _schedules.update { schedules.toList() }
                }
        }
    }

    fun saveSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val result = anniversaryRepository.insertSchedule(schedule.toDataEntity())
            if (result.isSuccessful) {
                refreshSchedules()

                _snackMsg.emit("성공적으로 일정이 등록되었습니다.")
            }
        }
    }

    fun updateSchedule(newSchedule: Schedule) {
        viewModelScope.launch {
            val result = anniversaryRepository.patchSchedule(newSchedule.id, newSchedule.toDataEntity())
            if (result.isSuccessful) {
                refreshSchedules()

                _snackMsg.emit("성공적으로 일정이 수정되었습니다.")
            }
        }
    }

    fun removeSchedule(scheduleId: String) {
        viewModelScope.launch {
            val result = anniversaryRepository.deleteSchedule(scheduleId)
            if (result) {
                refreshSchedules()

                _snackMsg.emit("성공적으로 일정이 삭제되었습니다.")
            }
        }
    }

    fun validateSchedule(schedule: Schedule): Boolean {
        var validateResult = true

        if (schedule.title.trim().isEmpty()) {
            viewModelScope.launch {
                _snackMsg.emit("제목을 입력해주세요.")
            }

            validateResult = false
        }

        return validateResult
    }

    private fun refreshSchedules() {
        val (selectYear, selectMonth, selectDay) = this.selectDate.value.split("-").map { it.toInt() }

        getSchedules(selectYear, selectMonth, selectDay)
    }
}