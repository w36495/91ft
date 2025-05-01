package com.w36495.senty.view.screen.gift.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftCategorySelectionViewModel @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository,
) : ViewModel() {
    val state = giftCategoryRepository.categories
        .map {
            val mapped = it.map { category -> category.toUiModel() }.toList()

            GiftCategorySelectionUiState.Success(mapped)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            GiftCategorySelectionUiState.Loading
        )

    fun loadGiftCategories() {
        viewModelScope.launch {
            viewModelScope.launch {
                giftCategoryRepository.fetchCategories()
                    .onFailure {
                        Log.d("GiftCategorySelectionVM", it.stackTraceToString())
                    }
            }
        }
    }
}


sealed interface GiftCategorySelectionUiState {
    data object Loading : GiftCategorySelectionUiState
    data class Success(val data: List<GiftCategoryUiModel>) : GiftCategorySelectionUiState
}