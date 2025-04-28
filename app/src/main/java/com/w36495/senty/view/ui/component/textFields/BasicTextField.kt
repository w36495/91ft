package com.w36495.senty.view.ui.component.textFields

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGray50
import com.w36495.senty.view.ui.theme.SentyGreen60

@Composable
fun SentyTextField(
    modifier: Modifier = Modifier,
    text: String,
    hint: String,
    hintSize: TextUnit = TextUnit.Unspecified,
    isError: Boolean = false,
    errorMsg: String,
    enabled: Boolean = true,
    inputType: KeyboardType = KeyboardType.Text,
    textStyle: TextStyle = LocalTextStyle.current,
    onChangeText: (String) -> Unit,
) {
    TextField(
        keyboardOptions = KeyboardOptions(
            keyboardType = inputType
        ),
        modifier = modifier,
        value = text,
        onValueChange = {
            onChangeText(it)
        },
        placeholder = {
            Text(
                text = hint,
                style = SentyTheme.typography.bodyMedium
                    .copy(SentyGray50),
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedIndicatorColor = SentyGreen60,
            focusedIndicatorColor = SentyGreen60,
            disabledContainerColor = Color.White,
            disabledIndicatorColor = SentyGreen60,
            disabledPlaceholderColor = Color.Unspecified,
            errorContainerColor = Color.White,
            cursorColor = SentyGreen60,
        ),
        singleLine = true,
        maxLines = 1,
        isError = isError,
        readOnly = !enabled,
        supportingText = {
            if (isError) {
                Text(
                    text = errorMsg,
                    style = SentyTheme.typography.bodySmall,
                )
            }
        },
        textStyle = textStyle,
    )
}