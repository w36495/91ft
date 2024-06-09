package com.w36495.senty.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.w36495.senty.data.domain.GiftCategoryEntity
import com.w36495.senty.data.domain.GiftCategoryPatchDTO
import com.w36495.senty.data.remote.service.GiftCategoryService
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.gift.GiftCategory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class GiftCategoryRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val giftRepository: GiftRepository,
    private val giftCategoryService: GiftCategoryService
) : GiftCategoryRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid

    override fun getCategories(): Flow<List<GiftCategory>> = flow {
        val result = giftCategoryService.getCategories(userId)
        val categories = mutableListOf<GiftCategory>()

        if (result.isSuccessful) {
            if (result.headers()["Content-length"]?.toInt() != 4) {
                result.body()?.let {
                    val responseJson = Json.parseToJsonElement(it.string())

                    responseJson.jsonObject.mapKeys { (key, jsonElement) ->
                        val category = Json.decodeFromJsonElement<GiftCategoryEntity>(jsonElement).toDomainEntity()
                            .apply { setId(key) }
                        categories.add(category)
                    }
                }
            }
        } else throw IllegalArgumentException(result.errorBody().toString())

        emit(categories)
    }

    override suspend fun initCategory(defaultCategory: GiftCategoryEntity): Response<ResponseBody> {
        return giftCategoryService.insertCategory(userId, defaultCategory)
    }

    override suspend fun insertCategory(category: GiftCategoryEntity): Response<ResponseBody> {
        return giftCategoryService.insertCategory(userId, category)
    }

    override suspend fun patchCategory(categoryKey: String, category: GiftCategoryPatchDTO): Response<ResponseBody> {
        return giftCategoryService.patchCategory(userId, categoryKey, category)
    }

    override suspend fun deleteCategory(categoryKey: String): Boolean {
        val result = giftCategoryService.deleteCategory(userId, categoryKey)

        coroutineScope {
            giftRepository.getGifts().map { gifts ->
                gifts.filter { it.category.id == categoryKey }
            }.collect { gifts ->
                gifts.forEach { gift ->
                    giftRepository.deleteGift(gift.id)
                }
            }
        }

        if (result.isSuccessful) {
            return result.headers()["Content-length"]?.toInt() == 4
        } else throw IllegalArgumentException("Failed to delete friend")
    }
}