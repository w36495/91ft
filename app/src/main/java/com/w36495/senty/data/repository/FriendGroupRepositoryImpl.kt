package com.w36495.senty.data.repository

import android.util.Log
import com.w36495.senty.data.domain.FriendGroupEntity
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEntity
import com.w36495.senty.data.remote.service.FriendGroupService
import com.w36495.senty.domain.entity.FriendGroup
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class FriendGroupRepositoryImpl @Inject constructor(
    private val friendGroupService: FriendGroupService,
    private val userRepository: UserRepository,
) : FriendGroupRepository {
    private val _friendGroups = MutableStateFlow<List<FriendGroup>>(emptyList())
    override val friendGroups: StateFlow<List<FriendGroup>>
        get() = _friendGroups.asStateFlow()

    override suspend fun getFriendGroup(friendGroupId: String): Result<FriendGroup> {
        return try {
            userRepository.runWithUid { userId ->
                val result = friendGroupService.getFriendGroup(userId, friendGroupId)

                if (result.isSuccessful) {
                    val body = result.body()?.string()

                    if (body != null && result.headers()["Content-length"]?.toInt() != 4) {
                        val responseJson = Json.parseToJsonElement(body)

                        val friendGroup = responseJson.jsonObject.map { (key, jsonElement) ->
                            Json.decodeFromJsonElement<FriendGroupEntity>(jsonElement).toDomain(key)
                        }.first()

                        Result.success(friendGroup)
                    } else {
                        Result.failure(Exception("친구 그룹 조회 실패"))
                    }
                } else Result.failure(Exception(""))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFriendGroups(): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val result = friendGroupService.getFriendGroups(userId)

                if (result.isSuccessful) {
                    val body = result.body()?.string()

                    if (body != null && result.headers()["Content-Length"]?.toIntOrNull() != 4) {
                        val responseJson = Json.parseToJsonElement(body)

                        val friendGroups = responseJson.jsonObject.map { (key, jsonElement) ->
                            Json.decodeFromJsonElement<FriendGroupEntity>(jsonElement).toDomain(key)
                        }

                        Log.d("FriendGroupRepo", "친구 그룹 조회 완료")
                        _friendGroups.update { friendGroups.sortedBy { it.name }.toList() }
                        Result.success(Unit)
                    } else {
                        _friendGroups.update { emptyList() }
                        Result.success(Unit)
                    }
                } else {
                    Result.failure(Exception("FriendGroup 요청 실패 : ${result.errorBody()?.string()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertFriendGroup(newFriendGroup: FriendGroup): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = friendGroupService.insertFriendGroup(userId, newFriendGroup.toEntity())

                if (response.isSuccessful) {
                    val generatedId = response.body()?.key

                    if (generatedId != null) {
                        getFriendGroups()
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("친구 그룹 key 생성 실패"))
                    }
                } else Result.failure(Exception("친구 그룹 등록 실패 : ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e))
        }
    }

    override suspend fun patchFriendGroup(friendGroup: FriendGroup): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val response = friendGroupService.patchFriendGroup(userId, friendGroup.id, friendGroup.toEntity())

                if (response.isSuccessful) {
                    getFriendGroups()
                    Result.success(Unit)
                } else Result.failure(Exception("친구 그룹 수정 실패 : ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFriendGroup(friendGroupKey: String): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val result = friendGroupService.deleteFriendGroup(userId, friendGroupKey)

                if (result.isSuccessful) {
                    getFriendGroups()
                    Result.success(Unit)
                } else Result.failure(Exception("친구 그룹 삭제 실패 : ${result.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}