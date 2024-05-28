package com.w36495.senty.data.remote.service

import com.w36495.senty.data.domain.EntityKeyDTO
import com.w36495.senty.data.domain.ScheduleEntity
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AnniversaryService {
    @GET("schedules/{userId}.json")
    suspend fun getSchedules(
        @Path("userId") userId: String
    ): Response<ResponseBody>

    @POST("schedules/{userId}.json")
    suspend fun insertSchedule(
        @Path("userId") userId: String,
        @Body schedule: ScheduleEntity
    ): Response<ResponseBody>

    @PATCH("schedules/{userId}/{scheduleId}.json")
    suspend fun patchSchedule(
        @Path("userId") userId: String,
        @Path("scheduleId") scheduleId: String,
        @Body schedule: ScheduleEntity
    ): Response<ResponseBody>

    @PATCH("schedules/{userId}/{scheduleId}.json")
    suspend fun patchScheduleKey(
        @Path("userId") userId: String,
        @Path("scheduleId") scheduleId: String,
        @Body body: EntityKeyDTO
    ): Response<ResponseBody>

    @DELETE("schedules/{userId}/{scheduleId}.json")
    suspend fun deleteSchedule(
        @Path("userId") userId: String,
        @Path("scheduleId") scheduleId: String
    ): Response<String>
}