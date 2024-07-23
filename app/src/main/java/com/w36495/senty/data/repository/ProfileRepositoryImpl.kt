package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.ProfileDTO
import com.w36495.senty.data.remote.service.ProfileService
import com.w36495.senty.domain.repository.ProfileRepository
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val profileService: ProfileService,
) : ProfileRepository {
    private val userId = firebaseAuth.currentUser!!.uid

    override suspend fun isInitialized(): Response<ResponseBody> {
        return profileService.isInitialized(userId)
    }

    override suspend fun patchInitialized(): Response<ResponseBody> {
        return profileService.patchInitialized(userId, ProfileDTO(isInitialized = true))
    }
}