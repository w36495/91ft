package com.w36495.senty.view.ui.component.textFields

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun SentyPasswordTextField(
    modifier: Modifier = Modifier,
    hint: String,
    password: String,
    isError: Boolean,
    errorMsg: String,
    onChangeText: (String) -> Unit,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    BasicTextFieldWithTrailing(
        modifier = modifier,
        text = password,
        inputType = KeyboardType.Password,
        hint = hint,
        onChangeText = onChangeText,
        trailingIcon = {
            val visibleIcon = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = visibleIcon, null)
            }
        },
        isError = isError,
        errorMsg = errorMsg,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}