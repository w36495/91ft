package com.w36495.senty.view.screen.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.viewModel.SignupViewModel
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyEmailTextField
import com.w36495.senty.view.ui.component.textFields.SentyPasswordTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    vm: SignupViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onSuccessSignup: () -> Unit,
) {
    val emailErrorMsg by vm.emailErrorMsg.collectAsState()
    val passwordErrorMsg by vm.passwordErrorMsg.collectAsState()
    val passwordCheckErrorMsg by vm.passwordCheckErrorMsg.collectAsState()
    val hasEmailError by vm.hasEmailError.collectAsState()
    val hasPasswordError by vm.hasPasswordError.collectAsState()
    val hasPasswordCheckError by vm.hasPasswordCheckError.collectAsState()
    val signupResult by vm.signupResult.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordCheck by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var passwordCheckVisible by rememberSaveable { mutableStateOf(false) }

    if (signupResult) onSuccessSignup()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "회원가입") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                EmailSection(
                    email = email,
                    onChangeEmail = {
                        email = it
                    },
                    isError = hasEmailError,
                    errorMsg = emailErrorMsg
                )
                Spacer(modifier = Modifier.height(32.dp))
                PasswordSection(
                    password = password,
                    passwordCheck = passwordCheck,
                    passwordVisible = passwordVisible,
                    passwordCheckVisible = passwordCheckVisible,
                    onClickVisible = {
                        passwordVisible = !passwordVisible
                    },
                    onClickPasswordCheckVisible = {
                        passwordCheckVisible = !passwordCheckVisible
                    },
                    onPasswordChange = {
                        password = it
                    },
                    onPasswordCheckChange = {
                        passwordCheck = it
                    },
                    isErrorPassword = hasPasswordError,
                    isErrorPasswordCheck = hasPasswordCheckError,
                    passwordErrorMsg = passwordErrorMsg,
                    passwordCheckErrorMsg = passwordCheckErrorMsg
                )
            }
            SentyFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                text = "확인",
                onClick = {
                    vm.createAccount(email, password, passwordCheck)
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
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "비밀번호를 재설정할 때 사용되는 이메일입니다.",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "실제 사용하시는 이메일을 입력해주세요.",
            style = MaterialTheme.typography.bodySmall
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
    isErrorPassword: Boolean,
    isErrorPasswordCheck: Boolean,
    passwordErrorMsg: String,
    passwordCheckErrorMsg: String,
    password: String,
    passwordCheck: String,
    onPasswordChange: (String) -> Unit,
    onPasswordCheckChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordCheckVisible: Boolean,
    onClickVisible: () -> Unit,
    onClickPasswordCheckVisible: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "비밀번호",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "영문자/숫자로 입력해주세요. (8자 이상)",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))

        SentyPasswordTextField(
            password = password,
            onChangeText = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            hint = "비밀번호를 입력해주세요.",
            passwordVisible = passwordVisible,
            isError = isErrorPassword,
            errorMsg = passwordErrorMsg,
            onClickVisible = onClickVisible
        )
        Spacer(modifier = Modifier.height(4.dp))
        SentyPasswordTextField(
            password = passwordCheck,
            onChangeText = { onPasswordCheckChange(it) },
            modifier = Modifier.fillMaxWidth(),
            hint = "비밀번호를 다시 입력해주세요.",
            passwordVisible = passwordCheckVisible,
            isError = isErrorPasswordCheck,
            errorMsg = passwordCheckErrorMsg,
            onClickVisible = onClickPasswordCheckVisible
        )
    }
}