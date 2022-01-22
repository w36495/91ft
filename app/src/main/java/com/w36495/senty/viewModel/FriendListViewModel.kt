package com.w36495.senty.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.data.repository.FriendRepository

class FriendListViewModel : ViewModel() {

    private val friendRepository: FriendRepository = FriendRepository()
    val friendList: LiveData<List<Friend>> = friendRepository.getFriendsList()

    /**
     * 친구 정보 등록
     */
    fun addFriendInfo(friend: Friend) {
        friendRepository.writeNewFriend(friend)
    }

    /**
     * 친구 정보 수정
     */
    fun updateFriendInfo(friend: Friend) {
        friendRepository.updateFriendInfo(friend)
    }

    /**
     * 친구 정보 삭제
     */
    fun removeFriend(friendKey: String) {
        friendRepository.deleteFriend(friendKey)
    }

}