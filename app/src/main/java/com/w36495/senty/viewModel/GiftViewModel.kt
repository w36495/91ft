package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.entity.gift.GiftType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
) : ViewModel() {
    private var _gifts = MutableStateFlow<List<Gift>>(emptyList())
    val gifts = _gifts.asStateFlow()

    fun getGifts() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts ->
                    gifts.map {
                        var gift = Gift(giftDetail = it.toDomainEntity())
                        if (it.imgUri.isNotEmpty()) {
                            coroutineScope {
                                val img = async { giftImgRepository.getGiftImages(it.id, it.imgUri) }
                                gift = gift.copy(imgPath = img.await())
                            }
                        }

                        gift
                    }
                }
                .collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }

    fun getReceivedGifts() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts ->
                    gifts.filter { it.giftType == GiftType.RECEIVED }
                }
                .map { gifts ->
                    gifts.map {
                        var gift = Gift(giftDetail = it.toDomainEntity())
                        if (it.imgUri.isNotEmpty()) {
                            coroutineScope {
                                val img = async { giftImgRepository.getGiftImages(it.id, it.imgUri) }
                                gift = gift.copy(imgPath = img.await())
                            }
                        }

                        gift
                    }
                }
                .collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }

    fun getSentGifts() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts ->
                    gifts.filter { it.giftType == GiftType.SENT }
                }
                .map { gifts ->
                    gifts.map {
                        var gift = Gift(giftDetail = it.toDomainEntity())
                        if (it.imgUri.isNotEmpty()) {
                            coroutineScope {
                                val img = async { giftImgRepository.getGiftImages(it.id, it.imgUri) }
                                gift = gift.copy(imgPath = img.await())
                            }
                        }

                        gift
                    }
                }
                .collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }
}