package com.w36495.senty.view.ui.component.chips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.ui.theme.SentyTheme

@Composable
fun FriendGroupChip(
    modifier: Modifier = Modifier,
    text: String? = null,
    chipColor: Color? = null,
    textColor: Color? = null,
) {
    Row (
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = chipColor?.let { it } ?: Color.Unspecified,
                    shape = CircleShape,
                )
        )
        Text(
            text = text ?: "",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = SentyTheme.typography.labelMedium,
            color = textColor?.let { it } ?: Color.Unspecified)
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendGroupChipPreview() {
    MaterialTheme {
        FriendGroupChip(
            text = "친구",
            chipColor = Color.LightGray,
        )
    }
}