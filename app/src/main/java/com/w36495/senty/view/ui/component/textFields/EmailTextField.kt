package com.w36495.senty.view.ui.component.textFields

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.w36495.senty.view.screen.ui.theme.SentyTheme

@Composable
fun SentyEmailTextField(
    text: String,
    hint: String,
    isError: Boolean,
    errorMsg: String,
    modifier: Modifier = Modifier,
    onChangeText: (String) -> Unit,
) {
    SentyTextField(
        modifier = modifier,
        text = text,
        hint = hint,
        onChangeText = onChangeText,
        inputType = KeyboardType.Email,
        isError = isError,
        errorMsg = errorMsg,
        textStyle = SentyTheme.typography.bodyMedium,
    )
}