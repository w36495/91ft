package com.w36495.senty.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.domain.entity.EditImage
import com.w36495.senty.domain.error.EditGiftError

import com.w36495.senty.domain.repository.GiftImageRepository
import com.w36495.senty.domain.usecase.UploadGiftImageUseCase
import com.w36495.senty.repository.FakeGiftImageFailRepository
import com.w36495.senty.repository.FakeGiftImageConditionFailRepository
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class UploadGiftImageUseCaseTest {
    private lateinit var useCase: UploadGiftImageUseCase
    private lateinit var giftImageRepository: GiftImageRepository
    private lateinit var giftImageFailRepository: GiftImageRepository
    private lateinit var giftImageConditionFailRepository: GiftImageRepository

    @Before
    fun setUp() {
        giftImageRepository = FakeGiftImageRepository()
        giftImageFailRepository = FakeGiftImageFailRepository()
        giftImageConditionFailRepository = FakeGiftImageConditionFailRepository()

        useCase = UploadGiftImageUseCase(giftImageRepository)
    }

    @Test
    fun `저장 성공 시, Success를 반환한다`() = runTest {
        // When : 저장 성공 시,
        val result = useCase(gift.id, images)

        // Then : Success를 반환한다
        Assert.assertTrue(result.isSuccess)
        Assert.assertEquals(3, giftImageRepository.getGiftImages(gift.id).getOrThrow().size)
    }

    @Test
    fun `저장 실패 시, ImageUploadFailed 오류를 반환한다`() = runTest {
        useCase = UploadGiftImageUseCase(giftImageFailRepository)

        // When : 저장 실패 시
        val result = useCase(gift.id, images)

        // Then : 오류를 반환한다
        Assert.assertTrue(result.isFailure)
        Assert.assertTrue(result.exceptionOrNull() is EditGiftError.ImageUploadFailed)
    }

    @Test
    fun `1장 이상의 이미지 저장 중 저장 실패 시, 저장에 성공한 이미지는 삭제된다`() = runTest {
        useCase = UploadGiftImageUseCase(giftImageConditionFailRepository)
        val failImage = mapOf(
            "image1" to giftNewImage,
            "fail_image2" to giftNewImage,
            "image3" to giftNewImage,
        )

        // When : 1장 이상의 이미지 저장 중 이미지 저장 실패 시,
        val result = useCase(gift.id, failImage)

        // Then : 저장에 성공한 이미지는 삭제된다
        Assert.assertTrue(result.isFailure)

        val savedImages = giftImageRepository.getGiftImages(gift.id).getOrThrow()
        Assert.assertEquals(0, savedImages.size)
    }

    companion object {
        private val friend = FriendUiModel(id = "1", name = "Friend 1")
        private val gift = GiftUiModel(id = "9", type = GiftType.RECEIVED, friendId = friend.id, friendName = friend.name)
        private val giftNewImage = EditImage.New(byteArrayOf(1, 2, 3))
        private val images = mapOf(
            "image1" to giftNewImage,
            "image2" to giftNewImage,
            "image3" to giftNewImage,
        )
    }
}