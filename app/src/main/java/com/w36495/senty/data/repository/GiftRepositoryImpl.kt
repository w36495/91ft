package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.GiftDetailEntity
import com.w36495.senty.data.domain.GiftImgUriDTO
import com.w36495.senty.data.remote.service.GiftService
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
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
    private val giftService: GiftService,
    private val giftImgRepository: GiftImgRepository,
) : GiftRepository {
    private var userId = firebaseAuth.currentUser!!.uid
    override fun getGift(giftId: String): Flow<GiftDetail> = flow {
        val result = giftService.getGift(userId, giftId)

        if (result.isSuccessful) {
            result.body()?.let {
                val responseJson = Json.parseToJsonElement(it.string())
                val giftDetailEntity = Json.decodeFromJsonElement<GiftDetailEntity>(responseJson)

                val giftCategory = GiftCategory.emptyCategory.apply { setId(giftDetailEntity.categoryId) }
                val friend = FriendDetail.emptyFriendEntity.apply { setId(giftDetailEntity.friendId) }
                val giftDetail = giftDetailEntity.toDomainEntity()
                    .apply { setId(giftId) }
                    .copy(category = giftCategory.copy(), friend = friend.copy())

                emit(giftDetail)

            }
        } else throw IllegalArgumentException(result.errorBody().toString())
    }

    override fun getGifts(): Flow<List<GiftDetail>> = flow {
        val result = giftService.getGifts(userId)

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())

                    responseJson.jsonObject.map { (key, jsonElement) ->
                        val giftDetailEntity = Json.decodeFromJsonElement<GiftDetailEntity>(jsonElement)
                        val tempGiftCategory = GiftCategory.emptyCategory.apply { setId(giftDetailEntity.categoryId) }
                        val tempFriend = FriendDetail.emptyFriendEntity.apply { setId(giftDetailEntity.friendId) }

                        giftDetailEntity.toDomainEntity()
                            .apply { setId(key) }
                            .copy(category = tempGiftCategory.copy(), friend = tempFriend.copy())
                    }.let { giftDetail ->
                        emit(giftDetail.toList())
                    }
                }
            } else emit(emptyList())
        } else throw IllegalArgumentException(result.errorBody().toString())
    }

    override suspend fun insertGift(gift: GiftDetailEntity): Response<ResponseBody> {
        return giftService.insertGift(userId, gift)
    }

    override suspend fun patchGift(giftId: String, gift: GiftDetailEntity): Response<ResponseBody> {
        return giftService.patchGift(userId, giftId, gift)
    }

    override suspend fun patchGiftImgUri(giftKey: String, giftUri: String): Response<ResponseBody> {
        val uri = GiftImgUriDTO(giftUri)

        return giftService.patchGiftImgUri(userId, giftKey, uri)
    }

    override suspend fun deleteGift(giftKey: String): Boolean {
        val result = giftService.deleteGift(userId, giftKey)

        if (result.isSuccessful) {
            return result.headers()["Content-length"]?.toInt() == 4
        } else throw IllegalArgumentException("Failed to delete gift")

    }
}