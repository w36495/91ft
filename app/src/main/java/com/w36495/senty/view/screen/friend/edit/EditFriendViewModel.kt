package com.w36495.senty.view.screen.friend.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.usecase.UpdateFriendUseCase
import com.w36495.senty.view.screen.friend.edit.contact.EditFriendContact
import com.w36495.senty.view.screen.friend.model.FriendUiModel
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
class EditFriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val updateFriendUseCase: UpdateFriendUseCase,
) : ViewModel() {
    private val _effect = Channel<EditFriendContact.Effect>()
    val effect = _effect.receiveAsFlow()

    private val _uiState = MutableStateFlow(EditFriendContact.State())
    val uiState = _uiState.asStateFlow()

    private val mutex = Mutex()

    fun getFriend(friendId: String) {
        viewModelScope.launch {
            updateLoadingState(true)

            val result = friendRepository.getFriend(friendId)

            result
                .onSuccess {
                    updateLoadingState(false)
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            friend = it.toUiModel(),
                            checkBirthdaySkipped = it.birthday.isEmpty(),
                        )
                    }
                }
                .onFailure {
                    sendEffect(EditFriendContact.Effect.ShowError(it))
                }
        }
    }

    fun handleEvent(event: EditFriendContact.Event) {
        when (event) {
            EditFriendContact.Event.OnClickFriendGroups -> {
                sendEffect(EditFriendContact.Effect.NavigateToFriendGroups)
            }
            EditFriendContact.Event.OnClickSave -> {
                if (!validateInput()) return

                saveFriend(uiState.value.friend)
            }
            EditFriendContact.Event.OnClickEdit -> {
                if (!validateInput()) return

                editFriend(uiState.value.friend)
            }
            EditFriendContact.Event.OnClickBack -> {
                sendEffect(EditFriendContact.Effect.NavigateToFriends)
            }
            EditFriendContact.Event.OnClickCalendar -> {
                _uiState.update { it.copy(showCalendarDialog = !it.showCalendarDialog) }
            }
            EditFriendContact.Event.OnClickFriendGroupSelectionDialog -> {
                _uiState.update { it.copy(showFriendGroupSelectionDialog = !it.showFriendGroupSelectionDialog) }
            }
            is EditFriendContact.Event.UpdateFriendName -> {
                updateFriendName(event.name)
            }
            is EditFriendContact.Event.UpdateFriendGroup -> {
                updateFriendGroup(event.group)
            }
            is EditFriendContact.Event.UpdateFriendBirthday -> {
                updateBirthday(event.birthday, event.checkBirthdaySkipped)
            }
            is EditFriendContact.Event.UpdateFriendMemo -> {
                updateMemo(event.memo)
            }
        }
    }
    private fun saveFriend(friend: FriendUiModel) {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            updateLoadingState(true)

            val result = mutex.withLock { friendRepository.insertFriend(friend.toDomain()) }

            result
                .onSuccess {
                    updateLoadingState(false)
                    _effect.send(EditFriendContact.Effect.ShowSnackBar("등록이 완료되었습니다."))
                }
                .onFailure {
                    updateLoadingState(false)
                    _effect.send(EditFriendContact.Effect.ShowError(it))
                }
        }
    }

    private fun editFriend(friend: FriendUiModel) {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            updateLoadingState(true)

            val result = mutex.withLock { updateFriendUseCase(friend.toDomain()) }

            result
                .onSuccess {
                    updateLoadingState(false)
                    _effect.send(EditFriendContact.Effect.ShowSnackBar("수정이 완료되었습니다."))
                }
                .onFailure {
                    updateLoadingState(false)
                    _effect.send(EditFriendContact.Effect.ShowError(it))
                }
        }
    }

    fun sendEffect(effect: EditFriendContact.Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    private fun updateFriendName(name: String) {
        _uiState.update { state ->
            state.copy(friend = state.friend.copy(name = name))
        }
    }

    private fun updateFriendGroup(friendGroup: FriendGroupUiModel) {
        _uiState.update { state ->
            state.copy(
                friend = state.friend.copy(
                    groupId = friendGroup.id,
                    groupName = friendGroup.name,
                    groupColor = friendGroup.color
                ),
                showFriendGroupSelectionDialog = false,
            )
        }
    }

    private fun updateBirthday(birthday: String, isSkipped: Boolean) {
        _uiState.update { state ->
            state.copy(
                friend = state.friend.copy(birthday = birthday),
                checkBirthdaySkipped = isSkipped,
                showCalendarDialog = false,
            )
        }
    }
    
    private fun updateMemo(memo: String) {
        _uiState.update { state ->
            state.copy(friend = state.friend.copy(memo = memo))
        }
    }

    private fun updateLoadingState(newState: Boolean) {
        _uiState.update { state ->
            state.copy(isLoading = newState)
        }
    }

    private fun validateInput(): Boolean {
        val validName = _uiState.value.friend.name.isNotEmpty()
        val validGroup = _uiState.value.friend.groupId.isNotEmpty() && _uiState.value.friend.groupName.isNotEmpty()

        _uiState.update {
            it.copy(
                isErrorName = !validName,
                isErrorGroup = !validGroup,
            )
        }

        return validName && validGroup
    }
}