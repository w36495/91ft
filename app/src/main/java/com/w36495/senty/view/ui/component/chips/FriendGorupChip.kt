package com.w36495.senty.view.ui.component.chips

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FriendGroupChip(
    modifier: Modifier = Modifier,
    text: String? = null,
    chipColor: Int? = null,
    textColor: Int? = null,
) {
    SuggestionChip(
        modifier = modifier,
        onClick = { },
        label = { Text(text = text ?: "") },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = chipColor?.let { Color(it) } ?: Color.Unspecified,
            labelColor = textColor?.let { Color(it) } ?: Color.Unspecified,
        ),
        border = BorderStroke(0.dp, Color.Transparent)
    )
}