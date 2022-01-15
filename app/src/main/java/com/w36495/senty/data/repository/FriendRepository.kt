package com.w36495.senty.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.w36495.senty.data.domain.Friend

class FriendRepository {

    private lateinit var database: DatabaseReference

    fun initializeDatabaseReference() {
        database = FirebaseDatabase.getInstance().reference
    }

    fun writeNewFriend(userId: String, friendName: String, friendPhone: String) {
        val friend = Friend(friendName, friendPhone, "img")
        database.child("users").child(userId).child("friends").setValue(friendName)
    }

}