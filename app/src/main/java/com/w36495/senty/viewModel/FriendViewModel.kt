package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository
) : ViewModel() {
    val friends: StateFlow<List<FriendEntity>> = combine(
        friendRepository.getFriends(),
        friendGroupRepository.getFriendGroups()
    ) { friends, groups ->
        friends.map { friend ->
            groups.find { group ->
                group.id == friend.groupId
            }?.let { group ->
                friend.toDomainEntity().apply {
                    setFriendGroup(group.toDomainModel())
                }
            } ?: throw IllegalStateException("Group not found")
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}