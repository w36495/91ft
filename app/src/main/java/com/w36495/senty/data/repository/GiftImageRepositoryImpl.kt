package com.w36495.senty.data.repository

import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

class GiftImageRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val userRepository: UserRepository,
) : GiftImageRepository {

    override suspend fun getGiftThumbs(giftId: String, imageName: String): Result<String> {
        return try {
            userRepository.runWithUid { userId ->
                Log.d("GiftImgRepo(getGiftThumbs)", "\n🎁 선물 썸네일 가져오는 중...")
                val imgPath = "images/gifts/$userId/$giftId/${imageName.plus(".jpg")}"

                val ref = firebaseStorage.reference.child(imgPath)

                val downloadUrl = ref.downloadUrl.await().toString()
                Log.d("GiftImgRepo(getGiftThumbs)", "\uD83C\uDF81 선물 썸네일 다운로드 완료!")
                Result.success(downloadUrl)
            }
        } catch (e: Exception) {
            Log.d("GiftImgRepo(getGiftThumbs)", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun getGiftImages(giftId: String): Result<List<String>> {
        return try {
            userRepository.runWithUid { userId ->
                Log.d("GiftImgRepo(getGiftImages)", "\n🎁 선물 이미지 가져오는 중...")
                val imgPath = "images/gifts/$userId/$giftId"

                val imgResult = firebaseStorage.reference.child(imgPath).listAll().await()
                val imagesWithoutThumbs = imgResult.items.filter { !it.name.contains("thumbs_") }

                val itemCount = imgResult.items.size
                Log.d("GiftImgRepo(getGiftImages)", "📸 다운로드할 이미지 개수: $itemCount")

                val downloadUrls = coroutineScope {
                    imagesWithoutThumbs
                        .sortedBy { it.name }
                        .map { ref ->
                            async { ref.downloadUrl.await().toString() }
                        }.awaitAll()
                }
                Log.d("GiftImgRepo(getGiftImages)", "\uD83C\uDF81 선물 이미지 다운로드 완료!")
                Result.success(downloadUrls.toList())
            }
        } catch (e: Exception) {
            Log.d("GiftImgRepo(getGiftImages)", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun insertGiftImageByBitmap(
        giftId: String,
        imageName: String,
        image: ByteArray
    ): Result<String> {
        return suspendCancellableCoroutine { cont ->
            val userId = userRepository.getUid()
            Log.d("GiftImage","🟢 ${if (imageName.contains("thumbs")) "썸네일" else "이미지"} 저장 시작" )
            val giftImagePath = "images/gifts/$userId/$giftId/$imageName.jpg"

            val uploadTask = firebaseStorage.reference
                .child(giftImagePath)
                .putBytes(image)

            uploadTask
                .addOnSuccessListener { task ->
                    if (task.task.isSuccessful) {
                        Log.d("GiftImage","🟢 ${if (imageName.contains("thumbs")) "썸네일" else "이미지"} 저장 완료" )
                        cont.resume(Result.success(imageName))
                    } else {
                        Log.d("GiftImage","🔴 ${if (imageName.contains("thumbs")) "썸네일" else "이미지"} 저장 실패" )
                        cont.resume(Result.failure(task.error ?: Exception("Unknown error")))
                    }
                }
        }
    }

    override suspend fun deleteGiftImage(giftId: String, imgPath: String): Result<String> {
        return try {
            userRepository.runWithUid { userId ->
                Log.d("GiftImage","🟢 이미지 삭제 시작" )
                val imagePath = "images/gifts/$userId/$giftId/$imgPath.jpg"
                firebaseStorage.reference.child(imagePath).delete().await()

                Log.d("GiftImage","🟢 이미지 삭제 완료" )
                Result.success(imgPath)
            }
        } catch (e: Exception) {
            Log.d("GiftImage","🔴 이미지 삭제 실패" )
            Log.d("GiftImageRepo(delete)", e.stackTraceToString())
            Result.failure(e)
        }
    }

    override suspend fun deleteAllGiftImage(giftId: String): Result<Unit> {
        return try {
            userRepository.runWithUid { userId ->
                val path = "images/gifts/$userId/$giftId"
                val giftDirRef = firebaseStorage.reference.child(path)

                val allItems = giftDirRef.listAll().await() // 모든 이미지 참조 가져오기

                if (allItems.items.isEmpty()) {
                    Log.d("GiftImageRepo(deleteAll)", "삭제할 이미지가 존재하지 않음")
                    Result.success(Unit)
                }

                coroutineScope {
                    allItems.items.map { itemRef ->
                        async {
                            itemRef.delete().await()
                        }
                    }
                }.awaitAll()

                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.w("GiftImageRepo(deleteAll)", "전체 이미지 삭제 실패", e)
            Result.failure(e)
        }
    }

    private fun generateImageName() = System.currentTimeMillis().toString()
}