package com.w36495.senty.view.ui.component.textFields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.w36495.senty.view.ui.theme.Green40

@Composable
fun BasicTextFieldWithTrailing(
    text: String,
    hint: String,
    isError: Boolean,
    errorMsg: String,
    inputType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable () -> Unit,
    visualTransformation: VisualTransformation,
    onChangeText: (String) -> Unit,
) {
    TextField(
        keyboardOptions = KeyboardOptions(
            keyboardType = inputType
        ),
        modifier = modifier,
        value = text,
        onValueChange = onChangeText,
        placeholder = {
            Text(text = hint)
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = Green40,
            focusedIndicatorColor = Green40,
            errorContainerColor = Color.White,
            cursorColor = Green40,
        ),
        singleLine = true,
        maxLines = 1,
        isError = isError,
        supportingText = {
            if (isError) {
                Text(text = errorMsg)
            }
        },
        trailingIcon = { trailingIcon() },
        visualTransformation = visualTransformation
    )
}