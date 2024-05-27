package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.ScheduleEntity
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface AnniversaryRepository {
    fun getSchedules(): Flow<List<ScheduleEntity>>
    suspend fun insertSchedule(schedule: ScheduleEntity): Response<ResponseBody>
}