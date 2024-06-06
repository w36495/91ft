package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.Friend
import com.w36495.senty.view.entity.gift.GiftType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
    private val giftRepository: GiftRepository,
) : ViewModel() {
    val friends: StateFlow<List<Friend>> = combine(
        friendRepository.getFriends(),
        friendGroupRepository.getFriendGroups()
    ) { friends, groups ->
        friends.map { friend ->
            val group = groups.find { it.id == friend.friendGroup.id }

            Friend(friendDetail = friend.copy(friendGroup = group!!))
        }
    }
        .combine(giftRepository.getGifts()) { friends, gifts ->
            friends.map { friend ->
                friend.copy(
                    sentGiftCount = gifts.filter { it.friendId == friend.friendDetail.id }.count { it.giftType == GiftType.SENT },
                    receivedGiftCount = gifts.filter { it.friendId == friend.friendDetail.id }.count { it.giftType == GiftType.RECEIVED }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}