package com.w36495.senty.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.w36495.senty.data.domain.Friend

class FriendRepository {

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid

    /**
     * 친구 정보 등록
     */
    fun writeNewFriend(friend: Friend) {
        val friendKey = database.push().key!!
        friend.key = friendKey
        database.child(userId).child("friends").child(friendKey).setValue(friend)
    }

    /**
     * 친구 목록 조회
     */
    fun getFriendsList(): LiveData<List<Friend>> {
        val friendListLiveData = MutableLiveData<List<Friend>>()
        val friendDatabase = FirebaseDatabase.getInstance().getReference(userId).child("friends")

        friendDatabase.addValueEventListener(object: ValueEventListener {
            // onDataChange : 경로의 전체 내용을 읽고 변경사항을 수신 대기
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val friendList = snapshot.children.map { friendSnapshot ->
                        friendSnapshot.getValue(Friend::class.java)!!
                    }
                    friendListLiveData.postValue(friendList)
                } else {
                    val friendList = emptyList<Friend>()
                    friendListLiveData.postValue(friendList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        return friendListLiveData
    }

    /**
     * 친구 정보 수정
     */
    fun updateFriendInfo(friend: Friend) {
        val postValues = friend.toMap()

        val childUpdate = hashMapOf<String, Any>(
            "/$userId/friends/${friend.key}/" to postValues
        )

        database.updateChildren(childUpdate)
    }

    /**
     * 친구 정보 삭제
     */
    fun deleteFriend(friendKey: String) {
        database.child(userId).child("friends").child(friendKey).removeValue()
    }

}