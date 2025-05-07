package com.w36495.senty.data.repository

import android.util.Log
import com.w36495.senty.data.domain.ScheduleEntity
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEntity
import com.w36495.senty.data.remote.service.AnniversaryService
import com.w36495.senty.domain.entity.Schedule
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class AnniversaryRepositoryImpl @Inject constructor(
    private val anniversaryService: AnniversaryService,
    private val userRepository: UserRepository,
) : AnniversaryRepository {
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    override val schedules: StateFlow<List<Schedule>>
        get() = _schedules.asStateFlow()

    override suspend fun fetchSchedules(): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val result = anniversaryService.getSchedules(userId)

                if (result.isSuccessful) {
                    val body = result.body()?.string()

                    if (body != null && result.headers()["Content-length"]?.toInt() != 4) {
                        val jsonElement = Json.parseToJsonElement(body)

                        val schedules = jsonElement.jsonObject.map { (key, jsonElement) ->
                            Json.decodeFromJsonElement<ScheduleEntity>(jsonElement).toDomain(key)
                        }.sortedBy { it.date }.toList()

                        _schedules.update { schedules }
                        Result.success(Unit)
                    } else {
                        _schedules.update { emptyList() }
                        Result.success(Unit)
                    }
                } else {
                    Result.failure(Exception(result.errorBody()?.toString()))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSchedule(scheduleId: String): Result<Schedule> {
        return try {
            val schedule = _schedules.value.filter { it.id == scheduleId }.firstOrNull()
            schedule?.let {
                Result.success(it)
            } ?: Result.failure(Exception("존재하지 않는 기념일입니다."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertSchedule(schedule: Schedule): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = anniversaryService.insertSchedule(userId, schedule.toEntity())

                if (response.isSuccessful) {
                    fetchSchedules()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("저장 실패하였습니다."))
                }
            }
        } catch (e: Exception) {
            Log.d("AnniversaryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun patchSchedule(schedule: Schedule): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = anniversaryService.patchSchedule(userId, schedule.id, schedule.toEntity())

                if (response.isSuccessful) {
                    fetchSchedules()
                    Result.success(Unit)
                } else {
                    Log.d("AnniversaryRepo", response.errorBody()?.toString() ?: "Error body is null")
                    Result.failure(Exception("수정 실패하였습니다."))
                }
            }
        } catch (e: Exception) {
            Log.d("AnniversaryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteSchedule(scheduleId: String): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = anniversaryService.deleteSchedule(userId, scheduleId)

                if (response.isSuccessful) {
                    val result = response.headers()["Content-length"]?.toInt() == 4

                    if (result) {
                        fetchSchedules()
                        Result.success(Unit)
                    } else Result.failure(Exception("삭제 실패하였습니다."))
                } else {
                    Log.d("AnniversaryRepo", response.errorBody()?.toString() ?: "Error body is null")
                    Result.failure(Exception("삭제 실패하였습니다."))
                }
            }
        } catch (e: Exception) {
            Log.d("AnniversaryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}