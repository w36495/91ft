package com.w36495.senty.view.screen.friend.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.local.datastore.FriendSyncFlagDataStore
import com.w36495.senty.data.mapper.toUiModel
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.domain.usecase.UpdateFriendUseCase
import com.w36495.senty.view.screen.friend.list.contact.FriendContact
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendSyncFlagDataStore: FriendSyncFlagDataStore,
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
    private val giftRepository: GiftRepository,
    private val updateFriendUseCase: UpdateFriendUseCase,
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
        .catch { _effect.send(FriendContact.Effect.ShowError(it)) }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FriendContact.State.Idle,
    )

    fun loadFriends() {
        viewModelScope.launch {
            runCatching {
                friendRepository.fetchFriends()
                friendGroupRepository.getFriendGroups()

                checkSync()
            }.onFailure { _effect.send(FriendContact.Effect.ShowError(it)) }
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

    private suspend fun checkSync() = withContext(Dispatchers.IO) {
        friendSyncFlagDataStore.load()?.let { requiredSync ->
            if (requiredSync) {
                // 선물 정보 불러와서 친구 정보 업데이트
                val updateResults = friendRepository.friends.value.map { friend ->
                    async {
                        val gifts = giftRepository.getGiftsByFriend(friend.id).getOrElse { emptyList() }

                        val receivedCount = gifts.count { it.type == GiftType.RECEIVED }
                        val sentCount = gifts.count { it.type == GiftType.SENT }

                        // 선물 개수가 다르다면
                        if (friend.sent != sentCount || friend.received != receivedCount) {
                            updateFriendUseCase(
                                friend.copy(received = receivedCount, sent = sentCount)
                            ).onFailure {
                                Log.e("FriendVM", "친구 선물 개수 동기화 실패(${friend.id})")
                            }
                        }
                    }
                }

                updateResults.awaitAll()
                friendSyncFlagDataStore.clear()
            }
        }
    }
}