package com.w36495.senty.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.LoginViewModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyEmailTextField
import com.w36495.senty.view.ui.component.textFields.SentyPasswordTextField
import com.w36495.senty.view.ui.theme.Green40

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onSuccessLogin: () -> Unit,
    onClickSignUp: () -> Unit,
) {
    val loginResult by vm.result.collectAsState()

    if (loginResult) onSuccessLogin()

    LoginContents(
        onClickLogin = { email, password ->
            vm.userLogin(email, password)
        },
        onClickSignUp = { onClickSignUp() }
    )
}

@Composable
private fun LoginContents(
    onClickLogin: (String, String) -> Unit,
    onClickSignUp: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showFindPasswordDialog by remember { mutableStateOf(false) }

    if (showFindPasswordDialog) {
        FindPasswordDialogScreen(
            onDismiss = { showFindPasswordDialog = false }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "91ft",
                style = MaterialTheme.typography.headlineLarge,
                color = Green40,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
            ) {
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "이메일을 입력하세요.",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.LightGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "비밀번호를 입력하세요.",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = Color.LightGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(8.dp))

                SentyFilledButton(text = "로그인", modifier = Modifier.fillMaxWidth()) {
                    onClickLogin(email, password)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "비밀번호 찾기", style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { showFindPasswordDialog = true })
                    Text(text = "회원가입", style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { onClickSignUp() }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmailSection(
    modifier: Modifier = Modifier,
    email: String,
    isError: Boolean,
    errorMsg: String,
    onChangeEmail: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "이메일",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))

        SentyEmailTextField(
            modifier = Modifier.fillMaxWidth(),
            text = email,
            hint = "이메일을 입력해주세요",
            onChangeText = onChangeEmail,
            isError = isError,
            errorMsg = errorMsg
        )
    }
}

@Composable
private fun PasswordSection(
    modifier: Modifier = Modifier,
    password: String,
    onPasswordChange: (String) -> Unit,
    isError: Boolean,
    errorMsg: String,
    passwordVisible: Boolean,
    onClickVisible: () -> Unit,
    onFindPassword: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "비밀번호",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))

        SentyPasswordTextField(
            modifier = Modifier.fillMaxWidth(),
            hint = "비밀번호를 입력해주세요.",
            password = password,
            onChangeText = onPasswordChange,
            isError = isError,
            errorMsg = errorMsg,
            onClickVisible = onClickVisible,
            passwordVisible = passwordVisible
        )

        Row(
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp)
                .clickable {
                    onFindPassword()
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "비밀번호 찾기",
                style = MaterialTheme.typography.labelLarge
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    SentyTheme {
        LoginContents(
            onClickLogin = { _, _ ->},
            onClickSignUp = {}
        )
    }
}