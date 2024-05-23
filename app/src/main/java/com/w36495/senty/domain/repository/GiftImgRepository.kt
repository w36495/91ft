package com.w36495.senty.domain.repository

import android.net.Uri

interface GiftImgRepository {
    fun insertGiftImgByBitmap(giftId: String, giftImg: ByteArray)
    fun insertGiftImgByUri(giftId: String, giftImg: Uri)
}