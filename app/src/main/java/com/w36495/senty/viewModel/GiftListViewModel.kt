package com.w36495.senty.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.w36495.senty.data.domain.Gift

class GiftListViewModel : ViewModel() {

    private val _giftList = MutableLiveData<ArrayList<Gift>>()
    private var giftListItem = arrayListOf<Gift>()

    val giftList: LiveData<ArrayList<Gift>> = _giftList

    fun addGift(gift: Gift) {
        giftListItem.add(gift)
        _giftList.value = giftListItem
    }

}