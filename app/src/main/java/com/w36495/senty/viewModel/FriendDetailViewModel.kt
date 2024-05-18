package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
) : ViewModel() {
    private var _snackMsg = MutableStateFlow("")
    val snackMsg: StateFlow<String> = _snackMsg.asStateFlow()
    private var _friend = MutableStateFlow(FriendEntity.emptyFriendEntity)
    val friend: StateFlow<FriendEntity> = _friend.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups().combine(
                friendRepository.getFriend(friendId)
            ) { groups, friend ->
                val group = groups.find { it.id == friend.groupId }
                friend.toDomainEntity().apply {
                    group?.toDomainModel()?.let { setFriendGroup(it) }
                }
            }.collect { friend ->
                _friend.update { friend }
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