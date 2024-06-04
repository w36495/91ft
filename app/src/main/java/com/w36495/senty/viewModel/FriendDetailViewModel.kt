package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
    private val giftRepository: GiftRepository,
) : ViewModel() {
    private var _snackMsg = MutableStateFlow("")
    val snackMsg: StateFlow<String> = _snackMsg.asStateFlow()
    private var _friend = MutableStateFlow(FriendDetail.emptyFriendEntity)
    val friend: StateFlow<FriendDetail> = _friend.asStateFlow()
    private var _gifts = MutableStateFlow<List<GiftDetailEntity>>(emptyList())
    val gifts: StateFlow<List<GiftDetailEntity>> = _gifts.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.getFriend(friendId).combine(
                friendGroupRepository.getFriendGroups()
            ) { friend, groups ->
                val group = groups.find { it.id == friend.groupId }?.toDomainModel()

                friend.toDomainEntity()
                    .copy(friendGroup = group!!)
                    .apply { setId(friendId) }
            }.collectLatest {
                _friend.value = it
            }
        }
    }

    fun getGifts(friendId: String) {
        viewModelScope.launch {
            giftRepository.getGifts()
                .map { gifts ->
                    gifts.filter { it.friendId == friendId }.map { it.toDomainEntity() }
                }.collectLatest {
                    _gifts.value = it
                }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            try {
                val result = async { friendRepository.deleteFriend(friendId) }.await()

                if (result) {
                    _snackMsg.update { "성공적으로 삭제되었습니다." }
                } else {
                    _snackMsg.update { "오류가 발생하였습니다." }
                }
            } catch (e: Exception) {
                Log.e("FriendDetailViewModel", "removeFriend: ", e)
            }
        }
    }
}