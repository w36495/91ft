package com.w36495.senty.viewModel

import androidx.lifecycle.*
import com.w36495.senty.data.domain.Gift
import com.w36495.senty.data.exception.StorageError
import com.w36495.senty.data.repository.GiftRepository

class GiftViewModel(friendKey: String) : ViewModel() {

    class GiftViewModelFactory(private val friendKey: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(GiftViewModel::class.java)) {
                GiftViewModel(friendKey) as T
            } else {
                throw IllegalArgumentException()
            }
        }
    }

    private val giftRepository = GiftRepository(friendKey)

    val giftList: LiveData<List<Gift>> = giftRepository.getGiftsList()
    val giftProgress: LiveData<Double> = giftRepository.progress

    private val _giftToast = MutableLiveData<String?>()
    val giftToast: LiveData<String?> = _giftToast

    /**
     * 선물 등록
     */
    fun saveGift(gift: Gift) {
        try {
            giftRepository.insertGift(gift)
        } catch (error: StorageError) {
            _giftToast.value = error.message
        }
    }

    /**
     * 선물 수정
     */
    fun updateGift(gift: Gift, oldImagePath: String?) {
        try {
            giftRepository.updateGift(gift, oldImagePath)
        } catch (error: StorageError) {
            _giftToast.value = error.message
        }
    }

    /**
     * 선물 삭제
     */
    fun removeGift(gift: Gift) {
        try {
            giftRepository.deleteGift(gift)
        } catch (error: StorageError) {
            _giftToast.value = error.message
        }
    }
}