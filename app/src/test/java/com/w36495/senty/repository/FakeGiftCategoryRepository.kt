package com.w36495.senty.repository

import android.util.Log
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.entity.GiftCategory
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeGiftCategoryRepository : GiftCategoryRepository {
    private val _categories = MutableStateFlow<List<GiftCategory>>(emptyList())
    override val categories: StateFlow<List<GiftCategory>>
        get() = _categories.asStateFlow()

    override suspend fun fetchCategories(): Result<Unit> {
        return try {
            _categories.update {
                List(10) {
                    GiftCategoryUiModel(id = "Category $it").toDomain()
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertCategory(category: GiftCategory): Result<Unit> {
        return try {
            _categories.update {
                it + category
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("GiftCategoryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: GiftCategory): Result<Unit> {
        return try {
            _categories.update {
                it.map { existing ->
                    if (existing.id == category.id) category else existing
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("GiftCategoryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            _categories.update {
                it.filterNot { it.id == categoryId }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("GiftCategoryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}