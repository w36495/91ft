package com.w36495.senty.view.screen.gift.edit.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.R
import com.w36495.senty.view.screen.ui.theme.SentyTheme

@Composable
fun FriendNameChip(
    modifier: Modifier = Modifier,
    friendName: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = Color(0xFFFBFBFB),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row (
            modifier = Modifier.padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = friendName,
                modifier = Modifier.padding(start = 12.dp),
                style = SentyTheme.typography.bodyMedium,
            )
            
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_close_black_24),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(14.dp)
                    .clip(CircleShape)
                    .clickable { onClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendNameChipPreview() {
    SentyTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FriendNameChip(friendName = "Friend Name", onClick = {})
            FriendNameChip(friendName = "Friend Name", onClick = {})
            FriendNameChip(friendName = "Friend Name", onClick = {})
        }

    }
}