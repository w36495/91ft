package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftCategoryPatchDTO
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.view.entity.gift.GiftCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

            } else {
                _errorFlow.emit("카테고리 등록 중 오류가 발생하였습니다.")
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

    fun updateCategory(categoryId: String, categoryName: String) {
        viewModelScope.launch {
            val updateCategory = GiftCategoryPatchDTO(name = categoryName)

            giftCategoryRepository.patchCategory(categoryId, updateCategory)
        }
    }
}