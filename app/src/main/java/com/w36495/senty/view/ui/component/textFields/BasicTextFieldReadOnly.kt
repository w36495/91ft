package com.w36495.senty.view.ui.component.textFields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.ui.component.chips.FriendGroupChip

@Composable
fun SentyReadOnlyTextField(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    textStyle: TextStyle = LocalTextStyle.current,
    showChip: Boolean = false,
    group: FriendGroupUiModel? = null,
    dividerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row {
            Spacer(modifier = Modifier.width(16.dp))

            if (showChip) {
                FriendGroupChip(
                    text = group?.name,
                    chipColor = group?.color,
                    textStyle = textStyle,
                )
            } else {
                Text(
                    text = text,
                    color = textColor,
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = textStyle,
                )
            }
        }

        if (showChip) Spacer(modifier = Modifier.height(8.dp))

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = dividerColor
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}