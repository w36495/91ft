package com.w36495.senty.domain.repository

import com.w36495.senty.data.domain.GiftCategoryEntity
import com.w36495.senty.data.domain.GiftCategoryPatchDTO
import com.w36495.senty.view.entity.gift.GiftCategory
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Response

interface GiftCategoryRepository {
    fun getCategories(): Flow<List<GiftCategory>>
    suspend fun initCategory(defaultCategory: GiftCategoryEntity): Response<ResponseBody>
    suspend fun insertCategory(category: GiftCategoryEntity): Response<ResponseBody>
    suspend fun patchCategory(categoryKey: String, category: GiftCategoryPatchDTO): Response<ResponseBody>
    suspend fun deleteCategory(categoryKey: String): Boolean
}