package com.w36495.senty.domain.repository

import com.w36495.senty.view.entity.SearchAddress
import kotlinx.coroutines.flow.Flow

interface MapSearchRepository {
    fun getSearchResult(query: String): Flow<List<SearchAddress>>
}