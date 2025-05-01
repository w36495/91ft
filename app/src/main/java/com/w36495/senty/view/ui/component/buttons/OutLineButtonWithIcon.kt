package com.w36495.senty.view.ui.component.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGreen60

@Composable
fun SentyOutlinedButtonWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier.height(42.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(width = 1.dp, SentyGreen60)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Black
            )
            Text(
                text = text,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SentyOutlinedButtonWithIconPreview() {
    SentyTheme {
        SentyOutlinedButtonWithIcon(
            text = "친구 등록",
            icon = Icons.Default.Add,
            onClick = {}
        )
    }
}