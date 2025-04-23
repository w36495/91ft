package com.w36495.senty.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.w36495.senty.domain.repository.GiftImgRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

class GiftImgRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth,
) : GiftImgRepository {
    private val userId: String
        get() = firebaseAuth.currentUser?.uid ?: throw IllegalStateException("User not logged in")

    override suspend fun getGiftImages(giftId: String): Result<List<String>> {
        return try {
            Log.d("GiftImgRepo", "\n🎁 선물 이미지 가져오는 중...")
            val imgPath = "images/gifts/$userId/$giftId"

            val imgResult = firebaseStorage.reference.child(imgPath).listAll().await()
            val itemCount = imgResult.items.size
            Log.d("GiftImgRepo", "📸 다운로드할 이미지 개수: $itemCount")

            val downloadUrls = coroutineScope {
                imgResult.items.sortedBy { it.name } // 필요 시 정렬
                    .map { ref ->
                        async { ref.downloadUrl.await().toString() }
                    }.awaitAll()
            }
            val end = System.currentTimeMillis()
            Log.d("GiftImgRepo", "\uD83C\uDF81 선물 이미지 다운로드 완료!")
            Result.success(downloadUrls.toList())
        } catch (e: Exception) {
            Log.d("GiftImgRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertGiftImageByBitmap(
        giftId: String,
        giftImage: ByteArray
    ): Result<Unit> {
        return suspendCancellableCoroutine { cont ->
            val imgName = generateImageName()
            val giftImagePath = "images/gifts/$userId/$giftId/$imgName.jpg"

            val uploadTask = firebaseStorage.reference
                .child(giftImagePath)
                .putBytes(giftImage)

            uploadTask
                .addOnSuccessListener { task ->
                    if (task.task.isSuccessful) {
                        Log.d("GiftImgRepo", "이미지 등록 성공")
                        cont.resume(Result.success(Unit))
                    } else {
                        cont.resume(Result.failure(task.error ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override suspend fun deleteGiftImage(giftId: String, imgPath: String): Result<Unit> {
        return try {
            val imagePath = "images/gifts/$userId/$giftId/$imgPath"
            firebaseStorage.reference.child(imagePath).delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("GiftImageRepo", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteAllGiftImage(giftId: String): Result<Unit> {
        return try {
            val path = "images/gifts/$userId/$giftId"
            val giftDirRef = firebaseStorage.reference.child(path)

            val allItems = giftDirRef.listAll().await() // 모든 이미지 참조 가져오기

            if (allItems.items.isEmpty()) {
                Log.d("GiftImageRepo", "삭제할 이미지가 존재하지 않음")
                return Result.success(Unit)
            }

            coroutineScope {
                allItems.items.map { itemRef ->
                    async {
                        itemRef.delete().await()
                    }
                }
            }.awaitAll()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.w("GiftImageRepo", "전체 이미지 삭제 실패", e)
            Result.failure(e)
        }
    }

    private fun generateImageName() = System.currentTimeMillis().toString()
}