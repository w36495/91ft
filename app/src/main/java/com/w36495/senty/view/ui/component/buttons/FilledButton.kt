package com.w36495.senty.view.ui.component.buttons

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGreen60

@Composable
fun SentyFilledButton(
    modifier: Modifier = Modifier,
    text: String,
    buttonColor: Color = SentyGreen60,
    textColor: Color = Color.White,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.height(48.dp),
        onClick = { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        enabled = enabled,
    ) {
        Text(
            text = text,
            color = textColor,
            style = SentyTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterVertically),
        )
    }
}