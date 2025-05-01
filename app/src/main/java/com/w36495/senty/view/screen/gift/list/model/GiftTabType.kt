package com.w36495.senty.view.screen.gift.list.model

import androidx.annotation.StringRes
import com.w36495.senty.R

enum class GiftTabType(
    @StringRes val title: Int,
    val num: Int,
) {
    ALL(
        title = R.string.gift_tab_title_all,
        num = 0,
    ),
    RECEIVED(
        title = R.string.gift_tab_title_received,
        num = 1,
    ),
    SENT(
        title = R.string.gift_tab_title_sent,
        num = 2,
    );

    companion object {
        fun find(index: Int): GiftTabType {
            return entries.find { it.num == index } ?: ALL
        }
    }
}