package com.w36495.senty.repository

import android.util.Log
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.entity.Gift
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeGiftRepository : GiftRepository {
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
            _gifts.update {
                List(10) {
                    GiftUiModel(
                        id = "Gift $it",
                        friendId = "Friend $it",
                        categoryId = "Category $it"
                    ).toDomain()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("GiftRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertGift(gift: Gift): Result<String> {
        return try {
            _gifts.update {
                it + gift
            }
            Result.success(gift.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGift(gift: Gift): Result<Unit> {
        return try {
            _gifts.update {
                it.map { existing ->
                    if (existing.id == gift.id) gift else existing
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteGift(giftId: String, refresh: Boolean): Result<Unit> {
        return try {
            _gifts.update { gifts ->
                gifts.filterNot { it.id == giftId }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("GiftRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}