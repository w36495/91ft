package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.view.entity.FriendGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendGroupViewModel @Inject constructor(
    private val friendGroupRepository: FriendGroupRepository
) : ViewModel() {
    private val _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg get() = _snackbarMsg.asSharedFlow()
    private var _errorMsg = MutableStateFlow("")
    val errorMsg = _errorMsg.asStateFlow()
    private var _friendGroups = MutableStateFlow<List<FriendGroup>>(emptyList())
    val friendGroups get() = _friendGroups.asStateFlow()

    init {
        refreshFriendGroups()
    }

    fun saveFriendGroup(friendGroup: FriendGroup) {
        viewModelScope.launch {
            val result = friendGroupRepository.insertFriendGroup(friendGroup.toDataEntity())
            if (result) {
                refreshFriendGroups()
                _snackbarMsg.emit("성공적으로 그룹이 추가되었습니다.")
            }
        }
    }

    fun validateFriendGroup(friendGroup: FriendGroup): Boolean {
        var isValid = true

        if (friendGroup.name.trim().isEmpty()) {
            isValid = false
            viewModelScope.launch {
                _errorMsg.update { "그룹 이름을 입력해주세요." }
            }
        }

        return isValid
    }

    fun resetErrorMsg() {
        viewModelScope.launch {
            _errorMsg.update { "" }
        }
    }

    fun updateFriendGroup(friendGroup: FriendGroup) {
        viewModelScope.launch {
            val result = friendGroupRepository.patchFriendGroup(friendGroup.id, friendGroup.toDataEntity())
            if (result.isSuccessful) {
                refreshFriendGroups()

                _snackbarMsg.emit("성공적으로 그룹이 수정되었습니다.")
            }
        }
    }

    fun removeFriendGroup(friendGroupId: String) {
        viewModelScope.launch {
            val result = friendGroupRepository.deleteFriendGroup(friendGroupId)
            if (result) {
                _snackbarMsg.emit("성공적으로 그룹이 삭제되었습니다.")
                refreshFriendGroups()
            }
        }
    }

    fun refreshFriendGroups() {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups()
                .catch { throwable ->
                    _snackbarMsg.emit("그룹을 불러오는 중 오류가 발생하였습니다.")
                }
                .collect { refreshGroups ->
                    _friendGroups.update { refreshGroups.toList() }
                }
        }
    }
}