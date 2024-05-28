package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.entity.Schedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
class AnniversaryViewModel @Inject constructor(
    private val anniversaryRepository: AnniversaryRepository
) : ViewModel() {
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules = _schedules.asStateFlow()

    fun getSchedules(selectYear: Int, selectMonth: Int, selectDay: Int) {
        viewModelScope.launch {
            anniversaryRepository.getSchedules()
                .map {
                    it.map { schedule -> schedule.toDomainEntity() }.filter { schedule ->
                        selectYear == schedule.getYear() && StringUtils.format2Digits(selectMonth).toInt() == schedule.getMonth() && StringUtils.format2Digits(selectDay).toInt() == schedule.getDay()
                    }
                }
                .collectLatest {
                    _schedules.value = it.toList()
                }
        }
    }

    fun saveSchedule(schedule: Schedule) {
        viewModelScope.launch {
            val result = anniversaryRepository.insertSchedule(schedule.toDataEntity())

            if (result.isSuccessful) {
                result.body()?.let {
                    val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                    val key = jsonObject["name"].toString().replace("\"", "")

                    val keyResult = anniversaryRepository.patchScheduleKey(key)
                    if (keyResult.isSuccessful) {
                        if (keyResult.body()?.string() == it.string()) {
                            // TODO : 등록 성공
                        }
                    } else {
                        // TODO : 등록 실패
                    }
                }
            }
        }
    }

    fun updateSchedule(newSchedule: Schedule) {
        viewModelScope.launch {
            anniversaryRepository.patchSchedule(newSchedule.toDataEntity())
        }
    }

    fun removeSchedule(scheduleId: String) {
        viewModelScope.launch {
            anniversaryRepository.deleteSchedule(scheduleId)
        }
    }
}