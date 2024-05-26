package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendKeyDTO
import com.w36495.senty.data.domain.GiftEntity
import com.w36495.senty.data.domain.GiftImgUriDTO
import com.w36495.senty.data.remote.service.GiftService
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class GiftRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val giftService: GiftService
) : GiftRepository {
    private var userId = firebaseAuth.currentUser!!.uid

    override fun getGifts(): Flow<List<GiftEntity>> = flow {
        val result = giftService.getGifts(userId)
        val gifts = mutableListOf<GiftEntity>()

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())
                    responseJson.jsonObject.forEach { jsonGift ->
                        val parseFriend = Json.decodeFromJsonElement<GiftEntity>(jsonGift.value)
                        gifts.add(parseFriend)
                    }
                }
            }
        } else throw IllegalArgumentException(result.errorBody().toString())

        emit(gifts)
    }

    override suspend fun insertGift(gift: GiftEntity): Response<ResponseBody> {
        return giftService.insertGift(userId, gift)
    }

    override suspend fun patchGiftKey(giftKey: String): Response<ResponseBody> {
        val newKey = FriendKeyDTO(giftKey)

        return giftService.patchGiftKey(userId, giftKey, newKey)
    }

    override suspend fun patchGiftImgUri(giftKey: String, giftUri: String): Response<ResponseBody> {
        val uri = GiftImgUriDTO(giftUri)

        return giftService.patchGiftImgUri(userId, giftKey, uri)
    }
}