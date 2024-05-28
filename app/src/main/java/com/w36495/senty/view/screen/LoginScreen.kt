package com.w36495.senty.view.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.LoginViewModel
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyEmailTextField
import com.w36495.senty.view.ui.component.textFields.SentyPasswordTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSuccessLogin: () -> Unit,
) {
    val hasEmailError by vm.hasEmailError.collectAsState()
    val emailErrorMsg by vm.emailErrorMsg.collectAsState()
    val hasPasswordError by vm.hasPasswordError.collectAsState()
    val passwordErrorMsg by vm.passwordErrorMsg.collectAsState()
    val loginResult by vm.result.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var showFindPasswordDialog by remember { mutableStateOf(false) }

    if (loginResult) onSuccessLogin()
    if (showFindPasswordDialog) {
        FindPasswordDialogScreen {
            showFindPasswordDialog = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "로그인") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                EmailSection(
                    modifier = Modifier.fillMaxWidth(),
                    email = email,
                    onChangeEmail = {
                        email = it
                    },
                    isError = hasEmailError,
                    errorMsg = emailErrorMsg
                )
                Spacer(modifier = Modifier.height(32.dp))

                PasswordSection(
                    modifier = Modifier.fillMaxWidth(),
                    password = password,
                    onPasswordChange = { password = it },
                    onFindPassword = { showFindPasswordDialog = true },
                    isError = hasPasswordError,
                    errorMsg = passwordErrorMsg,
                    passwordVisible = passwordVisible,
                    onClickVisible = { passwordVisible = !passwordVisible }
                )
            }

            SentyFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                text = "로그인",
                onClick = {
                    vm.userLogin(email, password)
                }
            )
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