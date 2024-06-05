package com.w36495.senty.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.w36495.senty.domain.repository.GiftImgRepository
import com.w36495.senty.util.ImgConverter
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class GiftImgRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
) : GiftImgRepository {
    private var userId: String = firebaseAuth.currentUser!!.uid
    override suspend fun getGiftImages(giftId: String, imgPath: String): String {
        return suspendCancellableCoroutine { continuation ->
            val imgPath = "images/gifts/$userId/$giftId/$imgPath"

            firebaseStorage.reference.child(imgPath).downloadUrl
                .addOnSuccessListener {
                    continuation.resume(it.toString())
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }

            continuation.invokeOnCancellation {
                Log.d("GiftImgRepositoryImpl", "Exception: (${it?.message.toString()})")
            }
        }
    }

    override suspend fun insertGiftImgByBitmap(
        giftId: String,
        giftImg: String
    ): String {
        return suspendCancellableCoroutine { continuation ->
            var imgName = generateImageName()
            val decodeImg = ImgConverter.stringToByteArray(giftImg)

            val giftPath = "images/gifts/$userId/$giftId/$imgName.jpg"
            firebaseStorage.reference.child(giftPath).putBytes(decodeImg)
                .addOnSuccessListener {
                    val path = it.storage.path.split("/")
                    continuation.resume(path[path.lastIndex])
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }

            continuation.invokeOnCancellation {
                Log.d("GiftImgRepositoryImpl", "Exception: (${it?.message.toString()})")
            }
        }
    }

    override suspend fun deleteGiftImg(imgPath: String): Boolean {
        var result = false

        firebaseStorage.reference.child(imgPath).delete()
            .addOnSuccessListener {
                result = true
            }
            .addOnFailureListener {
                throw IllegalArgumentException("Gift image delete Failed")
            }

        return result
    }

    private fun generateImageName() = System.currentTimeMillis().toString()
}