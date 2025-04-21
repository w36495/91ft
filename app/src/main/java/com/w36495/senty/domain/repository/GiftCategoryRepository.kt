package com.w36495.senty.domain.repository

import com.w36495.senty.domain.entity.GiftCategory
import kotlinx.coroutines.flow.StateFlow

interface GiftCategoryRepository {
    val categories: StateFlow<List<GiftCategory>>

    suspend fun fetchCategories(): Result<Unit>
    suspend fun insertCategory(category: GiftCategory): Result<Unit>
    suspend fun updateCategory(category: GiftCategory): Result<Unit>
    suspend fun deleteCategory(categoryKey: String): Boolean
}