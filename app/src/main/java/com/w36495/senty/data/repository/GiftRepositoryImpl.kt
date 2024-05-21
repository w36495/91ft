package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendKeyDTO
import com.w36495.senty.data.remote.service.GiftService
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.gift.GiftEntity
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class GiftRepositoryImpl @Inject constructor(
    private val giftService: GiftService
) : GiftRepository {
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override suspend fun insertGift(gift: GiftEntity): Response<ResponseBody> {
        return giftService.insertGift(userId, gift)
    }

    override suspend fun patchGiftKey(giftKey: String): Response<ResponseBody> {
        val newKey = FriendKeyDTO(giftKey)

        return giftService.patchGiftKey(userId, giftKey, newKey)
    }
}