package com.w36495.senty.view

import com.w36495.senty.data.domain.Friend

interface FriendSelectListener {

    fun onFriendInfoClicked(friend: Friend, position: Int)

}