package com.w36495.senty.view.ui.component.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SentyElevatedButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    ElevatedButton(
        modifier = modifier.height(42.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFBFBFB)
        )
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}