package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.ScheduleEntity
import com.w36495.senty.view.entity.Schedule
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface AnniversaryRepository {
    fun getSchedules(): Flow<List<Schedule>>
    suspend fun insertSchedule(schedule: ScheduleEntity): Response<ResponseBody>
    suspend fun patchSchedule(scheduleId: String, schedule: ScheduleEntity): Response<ResponseBody>
    suspend fun deleteSchedule(scheduleId: String): Boolean
}