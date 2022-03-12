package com.w36495.senty.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.data.exception.StorageError

class GiftRepository(friendKey: String) {

    private var database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var storage = FirebaseStorage.getInstance()

    private var userId: String = FirebaseAuth.getInstance().currentUser!!.uid
    val progress = MutableLiveData<Double>()

    private val imageBasePath = "images/$userId/${friendKey}/"
    private val databaseBasePath = "${userId}/gifts/${friendKey}/"

    /**
     * 선물 등록
     */
    fun insertGift(gift: Gift) {
        gift.key = database.push().key!!

        gift.imagePath?.let { imagePath ->
            val giftImagePath = System.currentTimeMillis().toString()
            storage.reference.child(imageBasePath + giftImagePath)
                .putFile(Uri.parse(imagePath))
                .addOnSuccessListener {
                    gift.imagePath = imageBasePath + giftImagePath
                    database.child(databaseBasePath + gift.key)
                        .setValue(gift)
                }
                .addOnProgressListener {
                    progress.postValue((100.0 * it.bytesTransferred) / it.totalByteCount)
                }
                .addOnFailureListener { }
        } ?: database.child(databaseBasePath + gift.key)
            .setValue(gift)
    }

    /**
     * 선물 목록 조회
     */
    fun getGiftsList(): LiveData<List<Gift>> {
        val giftListLiveData = MutableLiveData<List<Gift>>()
        val giftDatabase = FirebaseDatabase.getInstance().reference.child(databaseBasePath)

        giftDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val giftList = snapshot.children.map { giftSnapshot ->
                        giftSnapshot.getValue(Gift::class.java)!!
                    }
                    giftListLiveData.postValue(giftList)
                } else {
                    val giftList = emptyList<Gift>()
                    giftListLiveData.postValue(giftList)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        return giftListLiveData
    }

    /**
     * 선물 수정
     */
    fun updateGift(gift: Gift, oldImagePath: String?) {
        oldImagePath?.let { oldPath ->
            if (gift.imagePath == oldPath) {
                updateGiftInfoOnlyText(gift)
            } else {
                updateGiftInfo(gift)
                storage.reference.child(oldPath).delete()
                    .addOnSuccessListener { }
                    .addOnFailureListener { }
            }
        } ?: run {
            gift.imagePath?.let {
                updateGiftInfo(gift)
            } ?: updateGiftInfoOnlyText(gift)
        }
    }

    /**
     * 선물 삭제
     */
    fun deleteGift(gift: Gift) {
        database.child(databaseBasePath + gift.key)
            .removeValue()
        storage.reference.child(gift.imagePath!!).delete()
            .addOnSuccessListener { }
            .addOnFailureListener { }
    }

    /**
     * 선물 정보 수정 (텍스트 + 이미지)
     */
    private fun updateGiftInfo(gift: Gift) {
        val updateGiftImagePath = System.currentTimeMillis().toString()
        storage.reference.child(imageBasePath + updateGiftImagePath)
            .putFile(Uri.parse(gift.imagePath))
            .addOnSuccessListener {
                gift.imagePath = imageBasePath + updateGiftImagePath
                updateGiftInfoOnlyText(gift)
            }
            .addOnProgressListener {
                progress.postValue((100.0 * it.bytesTransferred) / it.totalByteCount)
            }
            .addOnFailureListener { }
    }

    /**
     * 선물 정보 수정 (텍스트)
     */
    private fun updateGiftInfoOnlyText(gift: Gift) {
        val giftValue = gift.toMap()
        val giftUpdate = hashMapOf<String, Any>(
            "${databaseBasePath}/${gift.key}" to giftValue
        )
        database.updateChildren(giftUpdate)
    }
}