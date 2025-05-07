package com.w36495.senty.view.screen.friendgroup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.usecase.UpdateFriendGroupUseCase
import com.w36495.senty.view.screen.friendgroup.model.EditFriendGroupContact
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class EditFriendGroupViewModel @Inject constructor(
    private val friendGroupRepository: FriendGroupRepository,
    private val updateFriendGroupUseCase: UpdateFriendGroupUseCase,
): ViewModel() {
    private val _effect = Channel<EditFriendGroupContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _uiState = MutableStateFlow<EditFriendGroupContact.State>(EditFriendGroupContact.State.Idle)
    val uiState get() = _uiState.asStateFlow()

    private val mutex = Mutex()

    fun handleEvent(event: EditFriendGroupContact.Event) {
        when (event) {
            is EditFriendGroupContact.Event.onSaveFriendGroup -> {
                saveFriendGroup(event.friendGroup)
            }
            is EditFriendGroupContact.Event.onEditFriendGroup -> {
                editFriendGroup(event.friendGroup)
            }
        }
    }

    private fun saveFriendGroup(newFriendGroup: FriendGroupUiModel) {
        viewModelScope.launch {
            _uiState.update { EditFriendGroupContact.State.Loading }

            val result = mutex.withLock { friendGroupRepository.insertFriendGroup(newFriendGroup.toDomain()) }

            result
                .onSuccess { _ ->
                    _uiState.update { EditFriendGroupContact.State.Success }
                    _effect.send(EditFriendGroupContact.Effect.NavigateFriendGroups("등록 완료되었습니다."))

                }
                .onFailure {
                    _uiState.update { EditFriendGroupContact.State.Idle }
                    Log.d("EditFriendGroupVM", it.stackTraceToString())
                    _effect.send(EditFriendGroupContact.Effect.ShowError("오류가 발생했습니다."))
                }
        }
    }

    private fun editFriendGroup(friendGroup: FriendGroupUiModel) {
        viewModelScope.launch {
            _uiState.update { EditFriendGroupContact.State.Loading }

            val result = updateFriendGroupUseCase(friendGroup.toDomain())

            result
                .onSuccess {
                    _effect.send(EditFriendGroupContact.Effect.NavigateFriendGroups("수정 완료되었습니다."))
                    _uiState.update { EditFriendGroupContact.State.Success }
                }
                .onFailure {
                    _uiState.update { EditFriendGroupContact.State.Idle }
                    Log.d("EditFriendGroupVM", it.stackTraceToString())
                    _effect.send(EditFriendGroupContact.Effect.ShowError("오류가 발생했습니다."))

                }
        }
    }
}