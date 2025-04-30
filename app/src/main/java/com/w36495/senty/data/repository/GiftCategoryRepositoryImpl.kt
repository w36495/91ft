package com.w36495.senty.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.GiftCategoryEntity
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toEntity
import com.w36495.senty.data.remote.service.GiftCategoryService
import com.w36495.senty.domain.entity.GiftCategory
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import javax.inject.Inject

class GiftCategoryRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val giftCategoryService: GiftCategoryService
) : GiftCategoryRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid

    private val _categories = MutableStateFlow<List<GiftCategory>>(emptyList())
    override val categories: StateFlow<List<GiftCategory>>
        get() =_categories.asStateFlow()

    override suspend fun fetchCategories(): Result<Unit> {
        return try {
            val result = giftCategoryService.fetchCategories(userId)

            if (result.isSuccessful) {
                val body = result.body()?.string()

                if (body != null && result.headers()["Content-length"]?.toInt() != 4) {
                    val responseJson = Json.parseToJsonElement(body)

                    val giftCategories = responseJson.jsonObject.map { (key, jsonElement) ->
                        Json.decodeFromJsonElement<GiftCategoryEntity>(jsonElement).toDomain(key)
                    }

                    _categories.update { giftCategories.sortedBy { category -> category.name }.toList() }
                    Result.success(Unit)
                } else {
                    _categories.update { emptyList() }
                    Result.success(Unit)
                }
            } else throw IllegalArgumentException(result.errorBody().toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchCategory(categoryId: String): Result<GiftCategory> {
        return try {
            val category = _categories.value.find { it.id == categoryId }
            category?.let {
                Result.success(it)
            } ?: Result.failure(Exception("카테고리가 존재하지 않습니다."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insertCategory(category: GiftCategory): Result<Unit> {
        return try {
            val response = giftCategoryService.insertCategory(userId, category.toEntity())
            if (response.isSuccessful) {
                fetchCategories()
                Result.success(Unit)
            } else Result.failure(Exception("선물 카테고리 등록 실패"))
        } catch (e: Exception) {
            Log.d("GiftCategoryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun updateCategory(category: GiftCategory): Result<Unit> {
        return try {
            val response = giftCategoryService.patchCategory(userId, category.id, category.toEntity())

            if (response.isSuccessful) {
                fetchCategories()
                Result.success(Unit)
            } else Result.failure(Exception("선물 카테고리 수정 실패"))
        } catch (e: Exception) {
            Log.d("GiftCategoryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            val response = giftCategoryService.deleteCategory(userId, categoryId)

            if (response.isSuccessful) {
                fetchCategories()
                Result.success(Unit)
            } else {
                Log.d("GiftCategoryRepo", response.errorBody().toString())
                Result.failure(Exception("선물 카테고리 삭제 실패 : ${response.errorBody().toString()}"))
            }
        } catch (e: Exception) {
            Log.d("GiftCategoryRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }
}