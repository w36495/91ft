package com.w36495.senty.view.ui.component.textFields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SentyPasswordTextField(
    hint: String,
    modifier: Modifier = Modifier,
    password: String,
    isError: Boolean,
    errorMsg: String,
    onChangeText: (String) -> Unit,
    passwordVisible: Boolean,
    onClickVisible: () -> Unit
) {
    BasicTextFieldWithTrailing(
        modifier = modifier,
        text = password,
        inputType = KeyboardType.Password,
        hint = hint,
        onChangeText = onChangeText,
        trailingIcon = {
            val visibleIcon = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = onClickVisible) {
                Icon(imageVector = visibleIcon, null)
            }
        },
        isError = isError,
        errorMsg = errorMsg,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}