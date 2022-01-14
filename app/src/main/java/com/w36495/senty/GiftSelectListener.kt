package com.w36495.senty

import com.w36495.senty.data.domain.Gift

interface GiftSelectListener {

    fun onGiftItemClicked(gift: Gift, position: Int)

}