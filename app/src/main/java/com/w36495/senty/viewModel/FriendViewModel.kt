package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendGroupRepository
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.domain.repository.GiftRepository
import com.w36495.senty.view.entity.Friend
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.entity.gift.GiftType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val friendGroupRepository: FriendGroupRepository,
    private val giftRepository: GiftRepository,
) : ViewModel() {
    private var _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends = _friends.asStateFlow()
    private var _friendGroups = MutableStateFlow(listOf(FriendGroup.allFriendGroup))
    val friendGroups = _friendGroups.asStateFlow()

    init {
        loadFriendGroups()
        loadFriends(groupFilter = null)
    }

    fun getFriendsByFriendGroup(friendGroup: FriendGroup?) {
        loadFriends(friendGroup)
    }

    private fun loadFriends(groupFilter: FriendGroup?) {
        viewModelScope.launch {
            combine(
                friendRepository.getFriends(),
                friendGroupRepository.getFriendGroups(),
                giftRepository.getGifts()
            ) { friends, groups, gifts ->
                friends.map { friend ->
                    val group = groups.find { group -> group.id == friend.friendGroup.id }
                    val sentGiftCount = gifts.filter { it.friend.id == friend.id }.count { it.giftType == GiftType.SENT }
                    val receivedGiftCount = gifts.filter { it.friend.id == friend.id }.count { it.giftType == GiftType.RECEIVED }

                    Friend(
                        friendDetail = friend.copy(friendGroup = group!!),
                        sentGiftCount = sentGiftCount,
                        receivedGiftCount = receivedGiftCount
                    )
                }
            }
                .map {
                    if (groupFilter != null) {
                        it.filter { friend ->
                            friend.friendDetail.friendGroup.id == groupFilter.id
                        }
                    } else it
                }
                .collect { collectFriends ->
                    _friends.update { collectFriends.toList() }
                }
        }
    }

    private fun loadFriendGroups() {
        viewModelScope.launch {
            friendGroupRepository.getFriendGroups()
                .collect {
                    _friendGroups.update { oldFriendGroups ->
                        oldFriendGroups.plus(it.toList())
                    }
                }
        }
    }
}