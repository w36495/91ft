package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.entity.gift.GiftEntity
import com.w36495.senty.view.entity.gift.GiftType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    private val friendRepository: FriendRepository,
    private val anniversaryRepository: AnniversaryRepository,
) : ViewModel() {
    private var _sentGifts = MutableStateFlow<List<GiftEntity>>(emptyList())
    val sentGifts = _sentGifts.asStateFlow()

    private var _receivedGifts = MutableStateFlow<List<GiftEntity>>(emptyList())
    val receivedGifts = _receivedGifts.asStateFlow()
    private var _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules = _schedules.asStateFlow()

    init {
        getSentGift()
        getReceivedGift()
        getSchedules()
    }

    private fun getSchedules() {
        viewModelScope.launch {
            anniversaryRepository.getSchedules()
                .map { schedules ->
                    schedules.filter { DateUtil.calRemainDate(it.date) > 0 }
                        .map { it.toDomainEntity() }
                }
                .collectLatest { schedules ->
                    val sortedSchedules = schedules.sortedBy { it.date }

                    if (schedules.size > 2) {
                        _schedules.value = sortedSchedules.subList(0, 1).toList()
                    }
                    else _schedules.value = sortedSchedules.toList()
                }
        }
    }

    private fun getSentGift() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts -> gifts.filter { it.giftType == GiftType.SENT } }
                .combine(friendRepository.getFriends()) { gifts, friends ->
                    gifts.map { gift ->
                        val friend = friends.find { it.id == gift.friendId }
                        var giftEntity = GiftEntity(
                            gift = gift.toDomainEntity(),
                            friend = friend!!.toDomainEntity()
                        )

                        if (gift.imgUri.isNotEmpty()) {
                            coroutineScope {
                                val img =
                                    async { giftImgRepository.getGiftImages(gift.id, gift.imgUri) }

                                giftEntity = giftEntity.copy(giftImg = img.await())
                            }
                        }

                        giftEntity
                    }
                }
                .collectLatest {
                    _sentGifts.value = it.toList()
                }
        }
    }

    private fun getReceivedGift() {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts -> gifts.filter { it.giftType == GiftType.RECEIVED } }
                .combine(friendRepository.getFriends()) { gifts, friends ->
                    gifts.map { gift ->
                        val friend = friends.find { it.id == gift.friendId }
                        var giftEntity = GiftEntity(
                            gift = gift.toDomainEntity(),
                            friend = friend!!.toDomainEntity()
                        )

                        if (gift.imgUri.isNotEmpty()) {
                            coroutineScope {
                                val img =
                                    async { giftImgRepository.getGiftImages(gift.id, gift.imgUri) }

                                giftEntity = giftEntity.copy(giftImg = img.await())
                            }
                        }

                        giftEntity
                    }
                }
                .collectLatest {
                    _receivedGifts.value = it.toList()
                }
        }
    }
}
