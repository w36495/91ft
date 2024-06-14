package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.FriendGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendAddViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
) : ViewModel() {
    private val _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg = _snackbarMsg.asSharedFlow()

    fun saveFriend(friend: FriendDetail) {
        viewModelScope.launch {
            val result = friendRepository.insertFriend(friend.toDataEntity())

            if (result.isSuccessful) {
                _snackbarMsg.emit("새로운 친구가 등록되었습니다.")
            } else {
                _snackbarMsg.emit("새로운 친구 등록을 실패하였습니다.")
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