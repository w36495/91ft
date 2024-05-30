package com.w36495.senty.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.w36495.senty.domain.repository.GiftImgRepository
import javax.inject.Inject

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
        var imgName = generateImageName()
        val decodeImg = ImgConverter.stringToByteArray(giftImg)

        val giftPath = "images/gifts/$userId/$giftId/$imgName.jpg"
        firebaseStorage.reference.child(giftPath).putBytes(giftImg)
            .addOnSuccessListener {
                onSuccess(it.storage.name)
            }
            .addOnFailureListener {
                throw IllegalArgumentException("Gift image upload Failed using Bitmap")
            }
    }

    override fun insertGiftImgByUri(giftId: String, giftImg: Uri, onSuccess: (String) -> Unit) {
        val imgName = generateImageName()

        val giftPath = "images/gifts/$userId/$giftId/$imgName.jpg"
        firebaseStorage.reference.child(giftPath).putFile(giftImg)
            .addOnSuccessListener {
                onSuccess(it.storage.name)
            }
            .addOnFailureListener {
                throw IllegalArgumentException("Gift image upload Failed using Uri")
            }
    }

    private fun generateImageName() = System.currentTimeMillis().toString()
}