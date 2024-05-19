package com.w36495.senty.view.ui.component.chips

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendGroupChip(
    modifier: Modifier = Modifier,
    text: String? = null,
    chipColor: Int? = null,
    textColor: Int? = null,
) {
    Chip(onClick = { },
        enabled = false,
        shape = RoundedCornerShape(10.dp),
        colors = ChipDefaults.chipColors(
            backgroundColor = chipColor?.let { Color(it) } ?: Color.Unspecified,
            disabledBackgroundColor = chipColor?.let { Color(it) } ?: Color.Unspecified,
        )

    ) {
        Text(
            text = text ?: "",
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = textColor?.let { Color(it) } ?: Color.Unspecified)
    }
}