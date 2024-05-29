package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import com.w36495.senty.view.entity.gift.GiftType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
) : ViewModel() {
    private var _gifts = MutableStateFlow<List<GiftDetailEntity>>(emptyList())
    val gifts = _gifts.asStateFlow()

    fun getGifts() {
        viewModelScope.launch {
            giftRepository.getGifts().map { gifts ->
                gifts.map {
                    it.toDomainEntity()
                }
            }
                .collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }

    fun getReceivedGifts() {
        viewModelScope.launch {
            giftRepository.getGifts().map { gifts ->
                gifts.filter { it.giftType == GiftType.RECEIVED }.map { it.toDomainEntity() }
            }
                .collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }

    fun getSentGifts() {
        viewModelScope.launch {
            giftRepository.getGifts().map { gifts ->
                gifts.filter { it.giftType == GiftType.SENT }.map { it.toDomainEntity() }
            }
                .collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }
}