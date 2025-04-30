package com.w36495.senty.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.GiftEntity
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEntity
import com.w36495.senty.data.remote.service.GiftService
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.threeten.bp.LocalDate
import javax.inject.Inject

class GiftRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val giftService: GiftService,
) : GiftRepository {
    private var userId = firebaseAuth.currentUser!!.uid
    private val _gifts = MutableStateFlow<List<Gift>>(emptyList())
    override val gifts: StateFlow<List<Gift>>
        get() = _gifts.asStateFlow()

    override suspend fun getGift(giftId: String): Result<Gift> {
        return try {
            val gift = gifts.value.first { it.id == giftId }

            Result.success(gift)
        } catch (e: Exception) {
            Log.d("GiftRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getGiftsByFriend(friendId: String): Result<List<Gift>> {
        return try {
            val gifts = gifts.value.filter { it.friendId == friendId }

            Result.success(gifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGiftsByCategoryId(categoryId: String): Result<List<Gift>> {
        return try {
            val gifts = gifts.value.filter { it.categoryId == categoryId }

            Result.success(gifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchGifts(): Result<Unit> {
        return try {
            Log.d("GiftRepo","🟢 선물 조회 시작")
            val response = giftService.fetchGifts(userId)

            if (response.isSuccessful) {
                val body = response.body()?.string()
                if (body != null && response.headers()["Content-length"]?.toInt() != 4) {
                    val responseJson = Json.parseToJsonElement(body)

                    val gifts = responseJson.jsonObject.map { (key, jsonElement) ->
                        Json.decodeFromJsonElement<GiftEntity>(jsonElement).toDomain(key)
                    }.toList().sortedByDescending { LocalDate.parse(it.date) }

                    Log.d("GiftRepo","🟢 선물 조회 완료")
                    _gifts.update { gifts }
                    Result.success(Unit)
                } else Result.success(Unit)
            } else {
                Log.d("GiftRepo", response.errorBody()?.toString() ?: "errorBody is null")
                Result.failure(Exception("오류가 발생했습니다."))
            }
        } catch (e: Exception) {
            Log.d("GiftRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertGift(gift: Gift): Result<String> {
        return try {
            Log.d("GiftRepo","🟢 선물 등록 시작")
            val response = giftService.insertGift(userId, gift.toEntity())

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Log.d("GiftRepo","🟢 선물 등록 완료")
                    Result.success(body.key)
                } else {
                    Log.d("GiftRepo","🔴 선물 등록 실패")
                    Result.failure(Exception("선물 등록 실패"))
                }
            } else {
                Log.d("GiftRepo","🔴 선물 등록 실패")
                Result.failure(Exception("선물 등록 실패"))
            }
        } catch (e: Exception) {
            Log.d("GiftRepo","🔴 선물 등록 실패")
            Result.failure(e)
        }
    }

    override suspend fun updateGift(gift: Gift): Result<Unit> {
        return try {
            Log.d("GiftRepo","🟢 선물 수정 시작")
            val response = giftService.patchGift(userId, gift.id, gift.copy(createdAt = gift.createdAt, updatedAt = System.currentTimeMillis()).toEntity())

            if (response.isSuccessful) {
                Log.d("GiftRepo","🟢 선물 수정 완료")
                Result.success(Unit)
            } else {
                Log.d("GiftRepo","🔴 선물 수정 실패")
                Result.failure(Exception("선물 수정 실패"))
            }
        } catch (e: Exception) {
            Log.d("GiftRepo","🔴 선물 수정 실패")
            Result.failure(e)
        }
    }

    override suspend fun deleteGift(giftId: String, refresh: Boolean): Result<Unit> {
        return try {
            val response = giftService.deleteGift(userId, giftId)

            if (response.isSuccessful) {
                if (refresh) fetchGifts()
                Log.d("GiftRepo", "선물 삭제 성공")
                Result.success(Unit)
            } else {
                Log.d("GiftRepo", "선물 삭제 실패 : ${response.errorBody()?.string()}")
                Result.failure(Exception("선물 삭제 실패"))
            }
        } catch (e: Exception) {
            Log.d("GiftRepo", e.stackTraceToString())
            Result.failure(e)
        }

    }
}