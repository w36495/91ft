package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.FriendEntity
import com.w36495.senty.data.domain.FriendKeyDTO
import com.w36495.senty.data.remote.service.FriendService
import com.w36495.senty.domain.repository.FriendRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val friendService: FriendService
) : FriendRepository {
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    override suspend fun insertFriend(friend: FriendEntity): Response<ResponseBody> {
        return friendService.insertFriend(userId, friend)
    }

    override suspend fun patchFriendId(friendId: String): Response<ResponseBody> {
        val newId = FriendKeyDTO(id = friendId)

        return friendService.patchFriendKey(userId, friendId, newId)
    }
}