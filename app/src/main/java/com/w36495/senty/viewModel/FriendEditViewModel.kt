package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendEditViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
) : ViewModel() {
    private var _errorFlow = MutableSharedFlow<String>()
    val errorFlow: SharedFlow<String> get() = _errorFlow.asSharedFlow()
    private var _friend = MutableStateFlow<FriendEditUiState>(FriendEditUiState.Loading)
    val friend: StateFlow<FriendEditUiState> = _friend.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.getFriend(friendId)
                .catch { _errorFlow.emit("정보를 불러오는 중 오류가 발생하였습니다.") }
                .collectLatest { friendDetail ->
                    friendGroupRepository.getFriendGroup(friendDetail.friendGroup.id)
                        .collectLatest { friendGroup ->
                            _friend.update { FriendEditUiState.Success(friendDetail.copy(friendGroup = friendGroup)) }
                        }
                }
        }
    }

    fun updateFriend(friend: FriendDetail) {
        viewModelScope.launch {
            friendRepository.patchFriend(friend.id, friend.toDataEntity())
        }
    }
}

sealed interface FriendEditUiState {
    data object Loading : FriendEditUiState
    data class Success(val friend: FriendDetail) : FriendEditUiState
}