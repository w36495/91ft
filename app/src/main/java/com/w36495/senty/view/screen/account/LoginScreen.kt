package com.w36495.senty.view.screen.account

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
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.viewModel.LoginViewModel
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.Green40
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onSuccessLogin: () -> Unit,
    onClickSignUp: () -> Unit,
) {
    val loginResult by vm.result.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        vm.errorFlow.collectLatest {
            snackBarHostState.showSnackbar(
                message = it,
            )
        }
    }

    if (loginResult || vm.autoLogin.value) onSuccessLogin()

    LoginContents(
        snackBarHostState = snackBarHostState,
        onClickLogin = { email, password, checkedAutoLogin ->
            vm.userLogin(email, password, checkedAutoLogin)
        },
        onClickSignUp = { onClickSignUp() }
    )
}

@Composable
private fun LoginContents(
    snackBarHostState: SnackbarHostState,
    onClickLogin: (String, String, Boolean) -> Unit,
    onClickSignUp: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checkedAutoLogin by remember { mutableStateOf(false) }
    var showFindPasswordDialog by remember { mutableStateOf(false) }

    if (showFindPasswordDialog) {
        FindPasswordDialogScreen(
            onDismiss = { showFindPasswordDialog = false }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Senty",
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkedAutoLogin,
                            onCheckedChange = {
                                checkedAutoLogin = !checkedAutoLogin
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Green40
                            )
                        )
                        
                        Text(
                            text = "자동 로그인",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    SentyFilledButton(text = "로그인", modifier = Modifier.fillMaxWidth()) {
                        onClickLogin(email, password, checkedAutoLogin)
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
}