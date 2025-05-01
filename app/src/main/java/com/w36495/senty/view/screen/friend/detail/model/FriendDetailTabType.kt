package com.w36495.senty.view.screen.friend.detail.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.w36495.senty.R

enum class FriendDetailTabType(@StringRes val title: Int, val num: Int) {
    INFORMATION(
        title = R.string.friend_detail_tab_info_text,
        num = 0,
    ),
    GIFT(
        title = R.string.friend_detail_tab_gift_text,
        num = 1,
    );

    companion object {
        @Composable
        fun getTabs(giftCount: Int): List<String> {
            return listOf(
                stringResource(id = INFORMATION.title),
                stringResource(id = GIFT.title).plus(" ($giftCount)")
            )
        }
    }
}