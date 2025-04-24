package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.Gift
import kotlinx.coroutines.flow.StateFlow
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftRepository {
    val gifts: StateFlow<List<Gift>>
    suspend fun getGift(giftId: String): Result<Gift>
    suspend fun getGiftsByFriend(friendId: String): Result<List<Gift>>
    suspend fun getGiftsByCategoryId(categoryId: String): Result<List<Gift>>
    suspend fun fetchGifts(): Result<Unit>
    suspend fun insertGift(gift: Gift): Result<String>
    suspend fun updateGift(gift: Gift): Result<Unit>
    suspend fun deleteGift(giftId: String, refresh: Boolean = true): Result<Unit>
}