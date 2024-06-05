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
    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()
    private var _friendGroups = MutableStateFlow<List<FriendGroup>>(emptyList())
    val friendGroups get() = _friendGroups.asStateFlow()

    init {
        refreshFriendGroups()
    }

    fun saveFriendGroup(friendGroup: FriendGroup) {
        viewModelScope.launch {
            val result = friendGroupRepository.insertFriendGroup(friendGroup.toDataEntity())
            if (result) refreshFriendGroups()
        }
    }

    fun updateFriendGroup(friendGroup: FriendGroup) {
        viewModelScope.launch {
            val result =
                friendGroupRepository.patchFriendGroup(friendGroup.id, friendGroup.toDataEntity())
            if (result.isSuccessful) refreshFriendGroups()
        }
    }

    fun removeFriendGroup(friendGroupId: String) {
        viewModelScope.launch {
            val result = friendGroupRepository.deleteFriendGroup(friendGroupId)
            if (result) refreshFriendGroups()
        }
    }

    private fun refreshFriendGroups() {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups()
                .catch { throwable ->
                    _errorFlow.emit(throwable)
                }
                .collect { refreshGroups ->
                    _friendGroups.update { refreshGroups.toList() }
                }
        }
    }
}