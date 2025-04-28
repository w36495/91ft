package com.w36495.senty.view.screen.gift.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.data.mapper.toGiftListUiModel
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.screen.gift.list.contact.GiftContact
import com.w36495.senty.view.screen.gift.list.model.GiftTabType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GiftViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImageRepository: GiftImgRepository,
) : ViewModel() {
    private val _selectedTab = MutableStateFlow(GiftTabType.ALL)

    private val _effect = Channel<GiftContact.Effect>()
    val effect = _effect.receiveAsFlow()

    val state = combine(
        giftRepository.gifts,
        _selectedTab
    ) { gifts, tab ->
        // 탭별 필터링
        gifts.filter {
            when (tab) {
                GiftTabType.RECEIVED -> it.type == GiftType.RECEIVED
                GiftTabType.SENT -> it.type == GiftType.SENT
                GiftTabType.ALL -> true
            }
        }
            .sortedBy { it.createdAt }
            .map { it.toGiftListUiModel() }
    }
        .flatMapLatest { uiList ->
            flow {
                emit(GiftContact.State(isLoading = true))            // 로딩 상태 emit
                // 이미지까지 붙여 준 뒤
                val enriched = coroutineScope {
                    uiList.map { gift ->
                        async {
                            giftImageRepository.getGiftImages(gift.id)
                                .map { imgs -> gift.copy(images = imgs) }
                                .getOrElse { gift }
                        }
                    }.awaitAll()
                }
                emit(
                    GiftContact.State(
                        isLoading = false,
                        gifts = enriched,
                    )
                )
            }
        }
        .stateIn(
            scope            = viewModelScope,
            started          = SharingStarted.WhileSubscribed(5_000),
            initialValue     = GiftContact.State(isLoading = true),
        )

    init {
        viewModelScope.launch {
            giftRepository.fetchGifts()
        }
    }

    fun handleEvent(event: GiftContact.Event) {
        when (event) {
            is GiftContact.Event.OnClickGift -> {
                sendEffect(GiftContact.Effect.NavigateToGiftDetail(event.giftId))
            }
            is GiftContact.Event.OnSelectTab -> {
                val selectedTab = GiftTabType.find(event.tab)

                _selectedTab.update { selectedTab }
            }
            GiftContact.Event.OnClickGiftCategories -> {
                sendEffect(GiftContact.Effect.NavigateToGiftCategories)
            }
            GiftContact.Event.OnClickBack -> {
                sendEffect(GiftContact.Effect.NavigateToBack)
            }
        }
    }

    private fun sendEffect(effect: GiftContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }
}