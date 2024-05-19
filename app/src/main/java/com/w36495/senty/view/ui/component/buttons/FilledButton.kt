package com.w36495.senty.view.ui.component.buttons

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.ui.theme.Green40

@Composable
fun SentyFilledButton(
    modifier: Modifier = Modifier,
    text: String,
    buttonColor: Color = Green40,
    textColor: Color = Color.White,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.height(42.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {
        Text(
            text = text,
            color = textColor
        )
    }
}