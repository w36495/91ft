package com.w36495.senty.usecase

import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.data.mapper.toDomain
import com.w36495.senty.domain.entity.EditImage
import com.w36495.senty.domain.usecase.SaveGiftUseCase
import com.w36495.senty.domain.usecase.UploadGiftImageUseCase
import com.w36495.senty.repository.FakeFriendRepository
import com.w36495.senty.repository.FakeGiftImageRepository
import com.w36495.senty.repository.FakeGiftRepository
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SaveGiftUseCaseTest {
    private lateinit var useCase: SaveGiftUseCase

    @Before
    fun setUp() {
        useCase = SaveGiftUseCase(
            giftRepository = FakeGiftRepository(),
            giftImageRepository = FakeGiftImageRepository(),
            friendRepository = FakeFriendRepository(),
            uploadGiftImageUseCase = UploadGiftImageUseCase(FakeGiftImageRepository()),
        )
    }

    @Test
    fun `이미지 저장 실패 시, 선물은 저장되지 않는다`() {

    }

    @Test
    fun `친구의 선물 카운트 수정 실패 시, 선물은 저장되지 않는다`() {

    }

    @Test
    fun `선물 데이터 저장 실패 시, 선물은 저장되지 않는다`() {

    }

    @Test
    fun `선물 데이터, 이미지, 친구의 선물 카운트 수정 성공 시, 선물은 저장된다`() = runTest {
        // When : 선물 데이터, 이미지, 친구의 선물 카운트 수정 성공 시
        val result = useCase(gift.toDomain(), mapOf(Pair("image1", giftNewImage)))

        // Then : 선물은 저장된다
        Assert.assertTrue(result.isSuccess)
    }

    companion object {
        private val friend = FriendUiModel(id = "1", name = "Friend 1")
        private val gift = GiftUiModel(id = "9", type = GiftType.RECEIVED, friendId = friend.id, friendName = friend.name)
        private val giftNewImage = EditImage.New(byteArrayOf(1, 2, 3))
    }
}