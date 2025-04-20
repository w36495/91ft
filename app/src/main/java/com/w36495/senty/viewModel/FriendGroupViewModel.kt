package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val friendGroups: StateFlow<List<FriendGroupUiModel>> = friendGroupRepository.friendGroups
        .map { domainList -> domainList.map { it.toUiModel() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun removeFriendGroup(friendGroupId: String) {
        viewModelScope.launch {
            val result = friendGroupRepository.deleteFriendGroup(friendGroupId)

            result
                .onSuccess { _snackbarMsg.emit("성공적으로 그룹이 삭제되었습니다.") }
                .onFailure {

                }
        }
    }

    fun sendSnackBar(msg: String) {
        viewModelScope.launch {
            _snackbarMsg.emit(msg)
        }
    }
}