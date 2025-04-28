package com.w36495.senty.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.screen.friend.list.contact.FriendContact
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
) : ViewModel() {
    private val _selectedFriendGroup = MutableStateFlow<FriendGroupUiModel?>(null)
    private val selectedFriendGroup get() = _selectedFriendGroup.asStateFlow()

    private val _effect = Channel<FriendContact.Effect>()
    val effect = _effect.receiveAsFlow()

    val uiState: StateFlow<FriendContact.State> = combine(
        friendRepository.friends,
        friendGroupRepository.friendGroups,
        selectedFriendGroup
    ) { friends, friendGroups, selectedGroup ->
        val allFriendGroups = listOf(FriendGroupUiModel.allFriendGroup) +
                friendGroups.map { it.toUiModel() }

        val filteredFriends = selectedGroup
            ?.let { sel -> friends.filter { it.groupId == sel.id } }
            ?: friends

        FriendContact.State.Success(
            friends = filteredFriends.map { it.toUiModel() },
            friendGroups = allFriendGroups,
        )
    }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FriendContact.State.Idle,
    )

    init {
        viewModelScope.launch {
            runCatching {
                friendRepository.fetchFriends()
                friendGroupRepository.getFriendGroups()
            }.onFailure { e ->
                Log.e("FriendVM", "fetch 중 에러", e)
            }
        }
    }

    fun handleEvent(event: FriendContact.Event) {
        when (event) {
            FriendContact.Event.OnClickFriendAdd -> {
                viewModelScope.launch {
                    _effect.send(FriendContact.Effect.NavigateToFriendAdd)
                }
            }
            FriendContact.Event.OnClickFriendGroups -> {
                viewModelScope.launch {
                    _effect.send(FriendContact.Effect.NavigateToFriendGroups)
                }
            }
            is FriendContact.Event.OnClickFriendDetail -> {
                viewModelScope.launch {
                    _effect.send(FriendContact.Effect.NavigateToFriendDetail(event.friendId))
                }
            }
            is FriendContact.Event.OnSelectFriendGroup -> {
                _selectedFriendGroup.update { event.friendGroup }
            }
        }
    }
}