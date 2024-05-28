package com.w36495.senty.data.repository

import com.w36495.senty.BuildConfig
import com.w36495.senty.data.remote.service.MapSearchService
import com.w36495.senty.di.NetworkModule
import com.w36495.senty.domain.repository.MapSearchRepository
import com.w36495.senty.view.entity.SearchAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MapSearchRepositoryImpl @Inject constructor(
    @NetworkModule.naver private val mapSearchService: MapSearchService
): MapSearchRepository {
    override fun getSearchResult(query: String): Flow<List<SearchAddress>> = flow {
        val resultAddress = mutableListOf<SearchAddress>()
        val result = mapSearchService.getSearch(
            clientKey = BuildConfig.NAVER_MAP_KEY,
            secretKey = BuildConfig.NAVER_MAP_SECRET_KEY,
            query = query
        )

        if (result.isSuccessful && result.body()?.status == "OK") {
            result.body()?.let { dto ->
                dto.addresses.forEach {
                    resultAddress.add(
                        SearchAddress(
                            address = it.roadAddress,
                            x = it.x,
                            y = it.y
                        )
                    )
                }

                emit(resultAddress.toList())
            }
        } else throw IllegalArgumentException("네이버 API 호출 실패")
    }
}