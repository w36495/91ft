package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.w36495.senty.data.domain.ScheduleEntity
import com.w36495.senty.data.remote.service.AnniversaryService
import com.w36495.senty.domain.repository.AnniversaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class AnniversaryRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val anniversaryService: AnniversaryService,
) : AnniversaryRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid

    override fun getSchedules(): Flow<List<ScheduleEntity>> = flow {
        val result = anniversaryService.getSchedules(userId)
        val schedules = mutableListOf<ScheduleEntity>()

        if (result.isSuccessful) {
            result.body()?.let {
                val jsonObject = JSONObject(it.string())

                jsonObject.keys().forEach { key ->
                    val jsonSchedules = jsonObject[key] as JSONObject
                    val entity: ScheduleEntity = Json.decodeFromString(jsonSchedules.toString())

                    schedules.add(entity)
                }
            }

            emit(schedules.toList())
        } else throw IllegalArgumentException("Failed to get schedules")
    }

    override suspend fun insertSchedule(schedule: ScheduleEntity): Response<ResponseBody> {
        return anniversaryService.insertSchedule(userId, schedule)
    }

    override suspend fun patchSchedule(schedule: ScheduleEntity): Response<ResponseBody> {
        return anniversaryService.patchSchedule(userId, schedule.id, schedule)
    }
}