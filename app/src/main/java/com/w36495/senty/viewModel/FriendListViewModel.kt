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
    fun addFriend(friend: Friend) {
        friendRepository.insertFriend(friend)
    }

    /**
     * 친구 정보 수정
     */
    fun updateFriend(friend: Friend, oldFriendImagePath: String?) {
        friendRepository.updateFriend(friend, oldFriendImagePath)
    }

    /**
     * 친구 정보 삭제
     */
    fun removeFriend(friend: Friend) {
        friendRepository.deleteFriend(friend)
    }
}