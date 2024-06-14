package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.FriendGroup
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
    private var _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg: SharedFlow<String> get() = _snackbarMsg.asSharedFlow()
    private var _friend = MutableStateFlow<FriendEditUiState>(FriendEditUiState.Loading)
    val friend: StateFlow<FriendEditUiState> = _friend.asStateFlow()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            friendRepository.getFriend(friendId)
                .catch { _snackbarMsg.emit("정보를 불러오는 중 오류가 발생하였습니다.") }
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
            val result = friendRepository.patchFriend(friend.id, friend.toDataEntity())

            if (result.isSuccessful) {
                _snackbarMsg.emit("친구 정보를 수정하였습니다.")
            } else {
                _snackbarMsg.emit("친구 정보를 수정하는 중 오류가 발생하였습니다.")
            }
        }
    }

    fun validateFriend(friend: FriendDetail, isCheckedBirthday: Boolean): Boolean {
        var isValid = true

        if (friend.name.isEmpty()) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("이름을 입력해주세요.")
            }
        } else if (friend.friendGroup == FriendGroup.emptyFriendGroup) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("친구 그룹을 선택해주세요.")
            }
        } else if (!isCheckedBirthday && friend.birthday.trim().isNullOrEmpty()) {
            isValid = false

            viewModelScope.launch {
                _snackbarMsg.emit("생일을 입력해주세요.")
            }
        }

        return isValid
    }
}

sealed interface FriendEditUiState {
    data object Loading : FriendEditUiState
    data class Success(val friend: FriendDetail) : FriendEditUiState
}