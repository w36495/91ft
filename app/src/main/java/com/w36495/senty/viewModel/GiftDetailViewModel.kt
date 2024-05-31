package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftDetail
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
class GiftDetailViewModel @Inject constructor(
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    private val giftCategoryRepository: GiftCategoryRepository,
    private val friendRepository: FriendRepository,
) : ViewModel() {
    private var _giftDetail = MutableStateFlow(GiftDetail.emptyGiftDetail)
    val giftDetail = _giftDetail.asStateFlow()

    fun getGiftDetail(giftId: String) {
        viewModelScope.launch {
            giftRepository.getGift(giftId)
                .map { it.toDomainEntity() }
                .combine(giftCategoryRepository.getCategories()) { gift, categories ->
                    val category = categories.map { it.toDomainEntity() }.find { it.id == gift.categoryId }!!

                    GiftDetail(
                        gift = gift,
                        category = category.copy(),
                        friend = FriendDetail.emptyFriendEntity
                    )
                }
                .map { gift ->
                    var newGiftDetail = gift.copy()

                    friendRepository.getFriend(gift.gift.friendId)
                        .map { it.toDomainEntity() }
                        .collectLatest {
                            newGiftDetail = gift.copy(friend = it.copy())
                        }

                    if (gift.gift.imgUri.isNotEmpty()) {
                        coroutineScope {
                            val img = async { giftImgRepository.getGiftImages(gift.gift.id, gift.gift.imgUri) }
                            newGiftDetail = newGiftDetail.copy(imgPath = img.await())
                        }
                    }

                    newGiftDetail
                }.collectLatest {
                    _giftDetail.value = it
                }
        }
    }
}