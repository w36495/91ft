package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.view.entity.gift.GiftCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftCategoryViewModel @Inject constructor(
    private val giftCategoryRepository: GiftCategoryRepository
) : ViewModel() {
    private var _categories = MutableStateFlow<List<GiftCategory>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            giftCategoryRepository.getCategories()
                .catch {  }
                .map { categories -> categories.map { it.toDomainEntity() } }
                .collectLatest {
                    _categories.value = it
                }
        }
    }
}