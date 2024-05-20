package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.GiftCategoryEntity
import com.w36495.senty.view.entity.gift.GiftCategory
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftCategoryRepository {
    fun getCategories(): Flow<List<GiftCategoryEntity>>
    suspend fun initCategory(defaultCategory: GiftCategory): Response<ResponseBody>
    suspend fun patchCategory(categoryKey: String): Response<ResponseBody>
}