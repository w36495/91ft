package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftCategoryPatchDTO
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.view.entity.gift.GiftCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftCategoryViewModel @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository
) : ViewModel() {
    private var _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg get() = _snackbarMsg.asSharedFlow()
    private var _errorMsg = MutableStateFlow("")
    val errorMsg = _errorMsg.asStateFlow()
    private val _categories = MutableStateFlow<List<GiftCategory>>(emptyList())
    val categories: StateFlow<List<GiftCategory>> = _categories.asStateFlow()

    init {
        refreshCategories()
    }

    private fun refreshCategories() {
        viewModelScope.launch {
            giftCategoryRepository.getCategories()
                .catch { _snackbarMsg.emit("카테고리를 불러오는 중 실패하였습니다.") }
                .collectLatest { giftCategories ->
                    _categories.update { giftCategories.toList() }
                }
        }
    }

    fun saveCategory(category: GiftCategory) {
        viewModelScope.launch {
            val result = giftCategoryRepository.insertCategory(category.toDataEntity())

            if (result.isSuccessful) {
                _snackbarMsg.emit("성공적으로 카테고리가 등록되었습니다.")
            } else {
                _snackbarMsg.emit("카테고리 등록 중 오류가 발생하였습니다.")
            }
        }
    }

    fun removeCategory(categoryKey: String) {
        viewModelScope.launch {
            val result = giftCategoryRepository.deleteCategory(categoryKey)

            if (result) {
                _snackbarMsg.emit("성공적으로 카테고리가 삭제되었습니다.")
                refreshCategories()
            }
            else _snackbarMsg.emit("카테고리 삭제 중 오류가 발생하였습니다.")
        }
    }

    fun updateCategory(categoryId: String, categoryName: String) {
        viewModelScope.launch {
            val updateCategory = GiftCategoryPatchDTO(name = categoryName)

            giftCategoryRepository.patchCategory(categoryId, updateCategory)
        }
    }

    fun validateGiftCategory(category: GiftCategory): Boolean {
        var isValid = true

        if (category.name.trim().isEmpty()) {
            isValid = false

            viewModelScope.launch {
                _errorMsg.emit("카테고리명을 입력해주세요.")
            }
        }

        return isValid
    }
}