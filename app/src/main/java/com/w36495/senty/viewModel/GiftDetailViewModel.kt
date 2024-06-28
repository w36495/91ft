package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftCategoryRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.gift.Gift
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
class GiftDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
    private val giftCategoryRepository: GiftCategoryRepository,
) : ViewModel() {
    private var _gift = MutableStateFlow(Gift.emptyGift)
    val gift = _gift.asStateFlow()

    fun getGift(giftId: String) {
        viewModelScope.launch {
            giftRepository.getGift(giftId)
                .map { giftDetail ->
                    var gift = Gift(giftDetail = giftDetail)
                    var giftImg = emptyList<String>()

                    coroutineScope {
                        val imgPath = async { giftImgRepository.getGiftImages(giftDetail.id) }.await()

                        giftImg = imgPath.toList()
                    }

                    val sortedGiftImage = giftImg.sortedBy {
                        it.split("/").run {
                            this[lastIndex].split("?")[0]
                        }.split("%2F")[1]
                    }

                    gift.copy(giftImages = sortedGiftImage.toList())
                }
                .collectLatest { gift ->
                    combine(friendRepository.getFriend(gift.giftDetail.friend.id),
                        giftCategoryRepository.getCategory(gift.giftDetail.category.id)) {
                        friend, category ->

                        gift.copy(
                            giftDetail = gift.giftDetail.copy(
                                friend = friend,
                                category = category
                            ),
                            giftImages = gift.giftImages
                        )
                    }.collectLatest { gift ->
                        _gift.update { gift }
                    }
                }
        }
    }

    fun removeGift(gift: Gift) {
        viewModelScope.launch {
            val result = giftRepository.deleteGift(gift.giftDetail.id)
            if (result) {
                if (gift.giftImages.isNotEmpty()) {
                    gift.giftImages.forEach { giftImage ->
                        val image = giftImage.split("/").run {
                            this[lastIndex].split("?")[0]
                        }.split("%2F")
                        giftImgRepository.deleteGiftImg(gift.giftDetail.id, image[image.lastIndex])
                    }
                }
            }
        }
    }
}