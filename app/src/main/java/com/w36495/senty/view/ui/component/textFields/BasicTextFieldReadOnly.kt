package com.w36495.senty.view.ui.component.textFields

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SentyReadOnlyTextField(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color,
    showChip: Boolean = false,
    chip: @Composable ((modifier: Modifier) -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (showChip && chip != null) {
            chip(Modifier.padding(vertical = 12.dp))
        } else {
            Text(
                text = text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 28.dp)
            )
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}