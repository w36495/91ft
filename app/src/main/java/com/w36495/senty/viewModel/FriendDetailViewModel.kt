package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendDetailViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
) : ViewModel() {
    private var _friend = MutableStateFlow(FriendEntity())
    val friend: StateFlow<FriendEntity> = _friend.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups().combine(
                friendRepository.getFriend(friendId)
            ) { groups, friend ->

                val group = groups.find { it.id == friend.groupId }

                friend.toDomainEntity().apply {
                    group?.let {
                        setFriendGroup(it.toDomainModel())
                    } ?: throw IllegalStateException("Group not found")
                }
            }
                .collect { friend ->
                    _friend.value = friend
                }
        }
    }
}