package com.w36495.senty.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.data.exception.StorageError
import com.w36495.senty.data.repository.FriendRepository

class FriendViewModel : ViewModel() {

    private val friendRepository: FriendRepository = FriendRepository()

    val friendList: LiveData<List<Friend>> = friendRepository.getFriendsList()
    val friendProgress: LiveData<Double> = friendRepository.progress

    private var _friendToast = MutableLiveData<String?>()
    val friendListToast: LiveData<String?> = _friendToast

    /**
     * 친구 정보 등록
     */
    fun addFriend(friend: Friend) {
        try {
            friendRepository.insertFriend(friend)
        } catch (error: StorageError) {
            _friendToast.value = error.message
        }

    }

    /**
     * 친구 정보 수정
     */
    fun updateFriend(friend: Friend, oldFriendImagePath: String?) {
        try {
            friendRepository.updateFriend(friend, oldFriendImagePath)
        } catch (error: StorageError) {
            _friendToast.value = error.message
        }
    }

    /**
     * 친구 정보 삭제
     */
    fun removeFriend(friend: Friend) {
        try {
            friendRepository.deleteFriend(friend)
        } catch (error: StorageError) {
            _friendToast.value = error.message
        }
    }

}