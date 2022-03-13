package com.w36495.senty.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import com.google.android.gms.tasks.Task
import com.w36495.senty.data.domain.Friend
import com.w36495.senty.data.exception.StorageError

class FriendRepository {

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var storage = FirebaseStorage.getInstance()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    val progress = MutableLiveData<Double>()

    /**
     * 유저 정보 삭제 (회원탈퇴)
     */
    fun deleteUser() {
        database.child(userId).removeValue()
        storage.reference.child("images/$userId").delete()
    }

    /**
     * 친구 정보 등록
     */
    fun insertFriend(friend: Friend) {
        friend.key = database.push().key!!

        if (friend.imagePath == null) {
            database.child(userId).child("friends").child(friend.key).setValue(friend)
        } else {
            val friendImageFileName = System.currentTimeMillis().toString()
            storage.reference.child("images/$userId/${friend.key}/$friendImageFileName")
                .putFile(Uri.parse(friend.imagePath))
                .addOnSuccessListener {
                    friend.imagePath = "images/$userId/${friend.key}/$friendImageFileName"
                    database.child(userId).child("friends").child(friend.key).setValue(friend)
                }
                .addOnProgressListener {
                    progress.postValue((100.0 * it.bytesTransferred) / it.totalByteCount)
                }
                .addOnFailureListener { exception ->
                    throw StorageError("insert Friend Exception", exception.cause!!)
                }
        }
    }

    /**
     * 친구 목록 조회
     */
    fun getFriendsList(): LiveData<List<Friend>> {
        val friendListLiveData = MutableLiveData<List<Friend>>()
        val friendDatabase = FirebaseDatabase.getInstance().getReference(userId).child("friends")

        friendDatabase.addValueEventListener(object : ValueEventListener {
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

            override fun onCancelled(error: DatabaseError) {}
        })
        return friendListLiveData
    }

    /**
     * 친구 정보 수정
     */
    fun updateFriend(friend: Friend, oldImagePath: String?) {
        oldImagePath?.let { path ->
            if (friend.imagePath == path) {
                updateFriendInfoOnlyText(friend)
            } else {
                updateFriendInfo(friend)
                storage.reference.child(path).delete()
                    .addOnSuccessListener { }
                    .addOnFailureListener { exception ->
                        throw StorageError("old image update friend error", exception.cause!!)
                    }
            }
        } ?: run {
            friend.imagePath?.let {
                updateFriendInfo(friend)
            } ?: updateFriendInfoOnlyText(friend)
        }
    }

    /**
     * 친구 정보 삭제
     */
    fun deleteFriend(friend: Friend) {
        database.child(userId).child("friends").child(friend.key).removeValue() // 친구 정보 삭제
        database.child(userId).child("gifts").child(friend.key).removeValue()   // 해당 친구의 선물 목록 삭제

        storage.reference.child("images/$userId/${friend.key}").listAll()
            .addOnSuccessListener { items ->
                items.items.forEach { item ->
                    item.delete()
                }
            }
            .addOnFailureListener { exception ->
                throw StorageError("delete friend error", exception.cause!!)
            }
    }

    /**
     * 친구 정보 수정 (텍스트 + 이미지)
     */
    private fun updateFriendInfo(friend: Friend) {
        val updateFriendImagePath = System.currentTimeMillis().toString()
        storage.reference.child("images/$userId/${friend.key}/$updateFriendImagePath")
            .putFile(Uri.parse(friend.imagePath))
            .addOnSuccessListener {
                friend.imagePath = "images/$userId/${friend.key}/$updateFriendImagePath"
                updateFriendInfoOnlyText(friend)
            }
            .addOnProgressListener {
                progress.postValue((100.0 * it.bytesTransferred) / it.totalByteCount)
            }
            .addOnFailureListener { exception ->
                throw StorageError("update friend(image_text error", exception.cause!!)
            }
    }

    /**
     * 친구 정보 수정 (텍스트)
     */
    private fun updateFriendInfoOnlyText(friend: Friend): Task<Void> {
        val postValues = friend.toMap()
        val childUpdate = hashMapOf<String, Any>(
            "/$userId/friends/${friend.key}/" to postValues
        )
        return database.updateChildren(childUpdate)
    }

}