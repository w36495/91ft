package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.view.entity.gift.GiftCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import javax.inject.Inject

@HiltViewModel
class GiftCategoryViewModel @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository
) : ViewModel() {
    val categories: StateFlow<List<GiftCategory>> = giftCategoryRepository.getCategories()
        .map { categories -> categories.map { it.toDomainEntity() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveCategory(category: GiftCategory) {
        viewModelScope.launch {
            val result = giftCategoryRepository.insertCategory(category.toDataEntity())

            if (result.isSuccessful) {
                result.body()?.let {
                    val jsonObject = Json.decodeFromString<JsonObject>(it.string())
                    val key = jsonObject["name"].toString().replace("\"", "")

                    val giftCategoryKeyResult = giftCategoryRepository.patchCategoryKey(key)
                    if (giftCategoryKeyResult.isSuccessful) {
                        if (giftCategoryKeyResult.body()?.string() == it.string()) {
                            // TODO : 등록 성공
                        }
                    } else {
                        // TODO : 등록 실패
                    }
                }
            } else {
                Log.d("GiftCategoryVM", result.errorBody().toString())
            }
        }
    }

    fun removeCategory(categoryKey: String) {
        viewModelScope.launch {
            val result = giftCategoryRepository.deleteCategory(categoryKey)

            if (result) {
                // TODO : 삭제 성공
            } else {
                // TODO : 삭제 실패
            }
        }
    }
}