package com.w36495.senty.view.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toHomeUiModel
import com.w36495.senty.domain.repository.AnniversaryRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.screen.home.contact.HomeContact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImageRepository,
    private val friendRepository: FriendRepository,
    private val anniversaryRepository: AnniversaryRepository,
) : ViewModel() {
    private val _effect = Channel<HomeContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _state = MutableStateFlow(HomeContact.State())
    val state get() = _state.asStateFlow()

    private var _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules = _schedules.asStateFlow()

    init {
        observeGifts()
        getSchedules()
    }

    fun handleEvent(event: HomeContact.Event) {
        when (event) {
            HomeContact.Event.OnClickGifts -> {
                sendEffect(HomeContact.Effect.NavigateToGifts)
            }
            is HomeContact.Event.OnClickGift -> {
                sendEffect(HomeContact.Effect.NavigateToGiftDetail(event.giftId))
            }
        }
    }

    private fun sendEffect(effect: HomeContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
    private suspend fun fetchInitialData() {
        viewModelScope.launch {
            giftRepository.fetchGifts()
            friendRepository.fetchFriends()
        }
    }

    private fun observeGifts() {
        viewModelScope.launch {
            Log.d("HomeVM","🟢 선물 조회 시작")
            _state.update {
                it.copy(
                    isReceivedGiftLoading = true,
                    isSentGiftLoading = true,
                )
            }

            fetchInitialData()

            // ➋ 방출된 목록 처리
            giftRepository.gifts
                .map { gifts ->
                    val received = gifts.filter { it.type == GiftType.RECEIVED }
                        .sortedByDescending { it.createdAt }
                        .take(9)
                        .map { it.toHomeUiModel() }
                    val sent     = gifts.filter { it.type == GiftType.SENT }
                        .sortedByDescending { it.createdAt }
                        .take(9)
                        .map { it.toHomeUiModel() }
                    received to sent
                }
                .catch {
                    Log.d("HomeVM","🔴 선물 조회 실패")
                }
                .collectLatest { (received, sent) ->
                    val enrichedReceivedGifts = coroutineScope {
                        received.map { gift ->
                            async {
                                gift.thumbnailName?.let { imageName ->
                                    giftImageRepository.getGiftThumbs(gift.id, imageName)
                                        .map { gift.copy(thumbnailPath = it) }
                                        .getOrElse { gift }
                                } ?: gift
                            }
                        }
                    }.awaitAll()

                    val enrichedSentGifts = coroutineScope {
                        sent.map { gift ->
                            async {
                                gift.thumbnailName?.let { imageName ->
                                    giftImageRepository.getGiftThumbs(gift.id, imageName)
                                        .map { gift.copy(thumbnailPath = it) }
                                        .getOrElse { gift }
                                } ?: gift
                            }
                        }
                    }.awaitAll()

                    _state.update {
                        it.copy(
                            isReceivedGiftLoading = false,
                            isSentGiftLoading = false,
                            receivedGifts = enrichedReceivedGifts,
                            sentGifts = enrichedSentGifts,
                        )
                    }

                    Log.d("HomeVM","🟢 선물 조회 완료")
                }
        }
    }


    fun getSchedules() {
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
}