package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.di.NetworkModule
import com.w36495.senty.domain.repository.MapSearchRepository
import com.w36495.senty.view.entity.SearchAddress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSearchViewModel @Inject constructor(
    @NetworkModule.naver private val mapSearchRepository: MapSearchRepository
) : ViewModel() {
    private var _search = MutableStateFlow<List<SearchAddress>>(emptyList())
    val search = _search.asStateFlow()

    fun getSearchResult(keyword: String) {
        viewModelScope.launch {
            mapSearchRepository.getSearchResult(keyword)
                .collectLatest {
                    _search.value = it
                }
        }
    }
}