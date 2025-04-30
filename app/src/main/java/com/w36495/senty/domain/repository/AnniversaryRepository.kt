package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.Schedule
import kotlinx.coroutines.flow.StateFlow

interface AnniversaryRepository {
    val schedules: StateFlow<List<Schedule>>

    suspend fun fetchSchedules(): Result<Unit>
    suspend fun getSchedule(scheduleId: String): Result<Schedule>
    suspend fun insertSchedule(schedule: Schedule): Result<Unit>
    suspend fun patchSchedule(schedule: Schedule): Result<Unit>
    suspend fun deleteSchedule(scheduleId: String): Result<Unit>
}