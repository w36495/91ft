package com.w36495.senty.view.screen.friendgroup.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.usecase.DeleteFriendGroupUseCase
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendGroupViewModel @Inject constructor(
    private val friendGroupRepository: FriendGroupRepository,
    private val deleteFriendGroupUseCase: DeleteFriendGroupUseCase,
) : ViewModel() {
    private val _snackbarMsg = MutableSharedFlow<String>()
    val snackbarMsg get() = _snackbarMsg.asSharedFlow()

    private var _errorChannel = Channel<Throwable?>()
    val errorFlow = _errorChannel.receiveAsFlow()

    val friendGroups: StateFlow<List<FriendGroupUiModel>> = friendGroupRepository.friendGroups
        .map { domainList -> domainList.map { it.toUiModel() } }
        .catch { _errorChannel.send(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )


    fun removeFriendGroup(friendGroupId: String) {
        viewModelScope.launch {

            deleteFriendGroupUseCase(friendGroupId)
                .onSuccess {
                    _snackbarMsg.emit("성공적으로 그룹이 삭제되었습니다.")
                }
                .onFailure {
                    Log.d("FriendGroupVM", it.stackTraceToString())
                    _errorChannel.send(it)
                }
        }
    }

    fun sendSnackBar(msg: String) {
        viewModelScope.launch {
            _snackbarMsg.emit(msg)
        }
    }
}