package com.w36495.senty.view.screen.login

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.screen.account.FindPasswordDialogScreen
import com.w36495.senty.view.screen.login.model.LoginEffect
import com.w36495.senty.view.screen.login.model.LoginFormState
import com.w36495.senty.view.screen.login.model.LoginUiState
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.SentyGray30
import com.w36495.senty.view.ui.theme.SentyGray50
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.antonFontFamily

@Composable
fun LoginRoute(
    vm: LoginViewModel = hiltViewModel(),
    moveToHome: () -> Unit,
    moveToSignUp: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()
    val formState by vm.formState.collectAsStateWithLifecycle()

    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                vm.loginWithGoogle(result.data, context)
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is LoginEffect.ShowError -> {

                }
                LoginEffect.NavigateToHome -> {
                    moveToHome()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        vm.launchGoogleSignIn.collect {
            googleAuthLauncher.launch(it)
        }
    }

    LoginScreen(
        formState = formState,
        isLoading = uiState == LoginUiState.Loading,
        onChangeEmail = { vm.updateEmail(it) },
        onChangePassword = { vm.updatePassword(it) },
        onChangeAutoLoginState = { vm.updateAutoLoginState(it) },
        onClickLogin = { vm.loginWithEmailAndPassword(context) },
        onClickSignUp = moveToSignUp,
        onClickKakao = { vm.signInWithKakao(context) },
        onClickGoogle = { vm.prepareSignInWithGoogle(context) },
    )
}

@Composable
private fun LoginScreen(
    modifier: Modifier = Modifier,
    formState: LoginFormState,
    isLoading: Boolean,
    onChangeEmail: (String) -> Unit,
    onChangePassword: (String) -> Unit,
    onChangeAutoLoginState: (Boolean) -> Unit,
    onClickLogin: () -> Unit,
    onClickSignUp: () -> Unit,
    onClickGoogle: () -> Unit,
    onClickKakao: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var showFindPasswordDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SentyWhite)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { focusManager.clearFocus() },
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = R.string.app_name).uppercase(),
                fontFamily = antonFontFamily,
                fontSize = 32.sp,
                color = SentyGreen60,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = formState.email, 
                    onValueChange = onChangeEmail,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.login_email_hint_text),
                            style = SentyTheme.typography.bodyMedium
                                .copy(color = SentyGray50)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = SentyGray30,
                        focusedBorderColor = SentyGreen60,),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = SentyTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = formState.password,
                    onValueChange = onChangePassword,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.login_password_hint_text),
                            style = SentyTheme.typography.bodyMedium
                                .copy(color = SentyGray50)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = SentyGray30,
                        focusedBorderColor = SentyGreen60
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    textStyle = SentyTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = PasswordVisualTransformation()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = formState.checkAutoLogin,
                        onCheckedChange = { onChangeAutoLoginState(!formState.checkAutoLogin) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = SentyGreen60
                        )
                    )

                    Text(
                        text = stringResource(id = R.string.login_auto_text),
                        style = SentyTheme.typography.labelMedium,
                    )
                }

                SentyFilledButton(
                    text = stringResource(id = R.string.login_title),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    onClick = {
                        focusManager.clearFocus()
                        if (formState.email.isNotEmpty() && formState.password.isNotEmpty()) {
                            onClickLogin()
                        }
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.login_find_passowrd_text),
                        style = SentyTheme.typography.labelMedium
                            .copy(color = SentyGray50),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { showFindPasswordDialog = true }
                    )

                    Text(
                        text = stringResource(id = R.string.signup_title),
                        style = SentyTheme.typography.labelMedium
                            .copy(color = SentyGray50),
                        modifier = Modifier
                            .padding(8.dp)
                            .padding(start = 4.dp)
                            .clickable { onClickSignUp() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            SocialLoginSection(
                modifier = Modifier.fillMaxWidth(),
                onGoogleLoginClick = onClickGoogle,
                onKakaoLoginClick = onClickKakao,
            )
        }

        when {
            showFindPasswordDialog -> {
                FindPasswordDialogScreen(
                    onDismiss = { showFindPasswordDialog = false }
                )
            }

            isLoading -> {
                LoadingCircleIndicator()
            }
        }
    }
}



@Composable
private fun SocialLoginSection(
    modifier: Modifier = Modifier,
    onGoogleLoginClick: () -> Unit,
    onKakaoLoginClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f))

            Text(
                text = "다음 계정으로 로그인",
                style = SentyTheme.typography.bodySmall
                    .copy(color = SentyGray60),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )
            
            Divider(modifier = Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_symbol_google),
                contentDescription = "Google Login Button",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onGoogleLoginClick() }
            )

//            Spacer(modifier = Modifier.width(24.dp))
//
//            Image(
//                painter = painterResource(id = R.drawable.img_kakao),
//                contentDescription = "Kakao Login Button",
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(CircleShape)
//                    .clickable { onKakaoLoginClick() }
//            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun LoginPreview() {
    SentyTheme {
        LoginScreen(
            formState = LoginFormState(),
            isLoading = false,
            onChangeEmail = {},
            onChangePassword = {},
            onChangeAutoLoginState = {},
            onClickLogin = {},
            onClickSignUp = {},
            onClickGoogle = {},
            onClickKakao = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialLoginsPreview() {
    SentyTheme {
        SocialLoginSection(
            onGoogleLoginClick = {},
            onKakaoLoginClick = {},
        )
    }
}



