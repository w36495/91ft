package com.w36495.senty.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.w36495.senty.domain.repository.FriendRepository
import com.w36495.senty.view.entity.FriendDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendAddViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
) : ViewModel() {
    fun saveFriend(friend: FriendDetail) {
        viewModelScope.launch {
            friendRepository.insertFriend(friend.toDataEntity())
        }
    }
}