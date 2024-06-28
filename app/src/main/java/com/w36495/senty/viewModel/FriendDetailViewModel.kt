package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.Gift
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
    private val giftRepository: GiftRepository,
    private val giftImgRepository: GiftImgRepository,
) : ViewModel() {
    private var _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> get() = _errorFlow.asSharedFlow()
    private var _deleteResult = MutableStateFlow(false)
    val deleteResult = _deleteResult.asStateFlow()
    private var _friend = MutableStateFlow(FriendDetail.emptyFriendEntity)
    val friend: StateFlow<FriendDetail> = _friend.asStateFlow()
    private var _gifts = MutableStateFlow<List<Gift>>(emptyList())
    val gifts: StateFlow<List<Gift>> = _gifts.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.getFriend(friendId)
                .catch { _errorFlow.emit("정보를 불러오는 중 오류가 발생하였습니다.") }
                .collectLatest { friendDetail ->
                    friendGroupRepository.getFriendGroup(friendDetail.friendGroup.id)
                        .collectLatest { friendGroup ->
                            _friend.update { friendDetail.copy(friendGroup = friendGroup) }
                        }
                }
        }
    }

    fun getGifts(friendId: String) {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts ->
                    gifts.filter { it.friend.id == friendId }.map { giftDetail ->
                        var gift = Gift(giftDetail = giftDetail)
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

                        gift.copy(giftImages = sortedGiftImg.toList())
                    }
                }.collectLatest {
                    _gifts.value = it.toList()
                }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            val result = friendRepository.deleteFriend(friendId)

            if (result) {
                _errorFlow.emit("성공적으로 삭제되었습니다.")
                _deleteResult.update { true }
            }
            else _errorFlow.emit("오류가 발생하였습니다.")
        }
    }
}