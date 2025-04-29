package com.w36495.senty.view.ui.component.textFields

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.ui.theme.SentyGreen60

@Composable
fun SentyMultipleTextField(
    modifier: Modifier = Modifier,
    text: String,
    minLines: Int = 3,
    onChangeText: (String) -> Unit,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    TextField(
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done,
        ),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (readOnly) MaterialTheme.colors.onSurface.copy(alpha = 0.12f) else SentyGreen60,
                shape = RoundedCornerShape(10.dp)
            ),
        value = text,
        onValueChange = {
            onChangeText(it)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = SentyGreen60,
        ),
        textStyle = textStyle,
        minLines = minLines,
        maxLines = 5,
        readOnly = readOnly
    )
}