package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.entity.gift.GiftType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    private val friendRepository: FriendRepository,
    private val anniversaryRepository: AnniversaryRepository,
) : ViewModel() {
    private var _sentGifts = MutableStateFlow<HomeGiftUiState>(HomeGiftUiState.Loading)
    val sentGifts = _sentGifts.asStateFlow()

    private var _receivedGifts = MutableStateFlow<HomeGiftUiState>(HomeGiftUiState.Loading)
    val receivedGifts = _receivedGifts.asStateFlow()
    private var _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules = _schedules.asStateFlow()

    fun loadAllDate() {
        getSchedules()
        getSentGifts()
        getReceivedGifts()
    }

    private fun getSchedules() {
        viewModelScope.launch {
            anniversaryRepository.getSchedules()
                .map { schedules ->
                    schedules.filter { DateUtil.calRemainDate(it.date) >= 0 }
                }
                .collectLatest { schedules ->
                    val sortedSchedules = schedules.sortedBy { it.date }

                    if (schedules.size > 2) {
                        _schedules.value = sortedSchedules.subList(0, 1).toList()
                    } else _schedules.value = sortedSchedules.toList()
                }
        }
    }

    private fun getSentGifts() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts -> gifts.filter { it.giftType == GiftType.SENT } }
                .combine(friendRepository.getFriends()) { gifts, friends ->
                    gifts.map { giftDetail ->
                        val friend = friends.find { friend -> friend.id == giftDetail.friend.id }
                        val gift = giftDetail.copy(friend = friend!!)
                        var giftImg = emptyList<String>()

                        coroutineScope {
                            val imgPath = async { giftImgRepository.getGiftImages(giftDetail.id) }.await()

                            giftImg = imgPath.toList()
                        }

                        val sortedGiftImg = giftImg.sortedBy {
                            it.split("/").run {
                                this[lastIndex].split("?")[0]
                            }.split("%2F")[1]
                        }
                        Gift(giftDetail = gift, giftImages = sortedGiftImg)
                    }
                }
                .collectLatest { gifts ->
                    if (gifts.isEmpty()) {
                        _sentGifts.update { HomeGiftUiState.Empty }
                    } else {
                        _sentGifts.update { HomeGiftUiState.Success(gifts.toList()) }
                    }
                }
        }
    }

    private fun getReceivedGifts() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts -> gifts.filter { it.giftType == GiftType.RECEIVED } }
                .combine(friendRepository.getFriends()) { gifts, friends ->
                    gifts.map { giftDetail ->
                        val friend = friends.find { friend -> friend.id == giftDetail.friend.id }
                        val gift = giftDetail.copy(friend = friend!!)
                        var giftImg = emptyList<String>()

                        coroutineScope {
                            val imgPath = async { giftImgRepository.getGiftImages(giftDetail.id) }.await()

                            giftImg = imgPath.toList()
                        }

                        val sortedGiftImg = giftImg.sortedBy {
                            it.split("/").run {
                                this[lastIndex].split("?")[0]
                            }.split("%2F")[1]
                        }
                        Gift(giftDetail = gift, giftImages = sortedGiftImg)
                    }
                }
                .collectLatest { gifts ->
                    if (gifts.isEmpty()) {
                        _receivedGifts.update { HomeGiftUiState.Empty }
                    } else {
                        _receivedGifts.update { HomeGiftUiState.Success(gifts.toList()) }
                    }
                }
        }
    }
}

sealed interface HomeGiftUiState {
    data object Loading: HomeGiftUiState
    data object Empty: HomeGiftUiState
    data class Success(val gifts: List<Gift>): HomeGiftUiState
}
