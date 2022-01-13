package com.w36495.senty.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.w36495.senty.data.domain.Friend

class FriendListViewModel : ViewModel() {

    private val _friendList = MutableLiveData<ArrayList<Friend>>()
    private val friendListItem : ArrayList<Friend> = arrayListOf()

    val friendList : LiveData<ArrayList<Friend>> = _friendList

    fun addFriendInfo(friend: Friend) {
        friendListItem.add(friend)
        _friendList.value = friendListItem
    }

}