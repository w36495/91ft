package com.w36495.senty.domain.repository

import android.net.Uri

interface GiftImgRepository {
    fun insertGiftImgByBitmap(giftId: String, giftImg: ByteArray, onSuccess: (String) -> Unit)
    fun insertGiftImgByUri(giftId: String, giftImg: Uri, onSuccess: (String) -> Unit)
}