package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendEditViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
) : ViewModel() {
    private var _friend = MutableStateFlow<FriendEditUiState>(FriendEditUiState.Loading)
    val friend: StateFlow<FriendEditUiState> = _friend.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.getFriend(friendId).combine(
                friendGroupRepository.getFriendGroups()
            ) { friend, groups ->
                val group = groups.find { it.id == friend.groupId }

                friend.toDomainEntity()
                    .copy(friendGroup = group!!)
                    .apply { setId(friendId) }
            }.collectLatest { friendDetail ->
                _friend.value = FriendEditUiState.Success(friendDetail)
            }
        }
    }

    fun updateFriend(friend: FriendDetail) {
        viewModelScope.launch {
            friendRepository.patchFriend(friend.toDataEntity())
        }
    }
}

sealed interface FriendEditUiState {
    data object Loading : FriendEditUiState
    data class Success(val friend: FriendDetail) : FriendEditUiState
}