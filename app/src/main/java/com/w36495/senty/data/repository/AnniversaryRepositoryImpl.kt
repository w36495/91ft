package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.ScheduleEntity
import com.w36495.senty.data.remote.service.AnniversaryService
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.view.entity.Schedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class AnniversaryRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val anniversaryService: AnniversaryService,
) : AnniversaryRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid

    override fun getSchedules(): Flow<List<Schedule>> = flow {
        val result = anniversaryService.getSchedules(userId)

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val jsonElement = Json.parseToJsonElement(it.string())

                    jsonElement.jsonObject.map { (key, jsonElement) ->
                        Json.decodeFromJsonElement<ScheduleEntity>(jsonElement)
                            .toDomainEntity().apply { setId(key) }
                    }.let { schedules ->
                        emit(schedules.toList())
                    }
                }
            }
        } else throw IllegalArgumentException("Failed to get schedules")
    }

    override suspend fun insertSchedule(schedule: ScheduleEntity): Response<ResponseBody> {
        return anniversaryService.insertSchedule(userId, schedule)
    }

    override suspend fun patchSchedule(scheduleId: String, schedule: ScheduleEntity): Response<ResponseBody> {
        return anniversaryService.patchSchedule(userId, scheduleId, schedule)
    }

    override suspend fun deleteSchedule(scheduleId: String): Boolean {
        val result = anniversaryService.deleteSchedule(userId, scheduleId)

        if (result.isSuccessful) {
            return result.headers()["Content-length"]?.toInt() == 4
        } else throw IllegalArgumentException("Failed to delete schedule")
    }
}