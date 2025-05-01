package com.w36495.senty.view.screen.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.component.SentyCenterAlignedTopAppBar
import com.w36495.senty.view.screen.signup.model.SignUpEffect
import com.w36495.senty.view.screen.signup.model.SignUpFormState
import com.w36495.senty.view.screen.signup.model.SignUpUiState
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.textFields.SentyEmailTextField
import com.w36495.senty.view.ui.component.textFields.SentyPasswordTextField
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun SignUpRoute(
    vm: SignUpViewModel = hiltViewModel(),
    padding: PaddingValues,
    moveToLogin: () -> Unit,
    onBackPressed: () -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val context = LocalContext.current

    val uiState by vm.uiState.collectAsState()
    val formState by vm.formState.collectAsStateWithLifecycle()

    val isEmailValid by vm.isEmailValid.collectAsStateWithLifecycle()
    val isPasswordValid by vm.isPasswordValid.collectAsStateWithLifecycle()
    val isPasswordMatch by vm.isPasswordMatch.collectAsStateWithLifecycle()
    val isFormValid by vm.isFormValid.collectAsStateWithLifecycle()

    var showErrorDialog by remember { mutableStateOf<String?>(null) }
    var showCompleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            if (effect is SignUpEffect.ShowError) {
                onShowGlobalErrorSnackBar(effect.throwable)
            }
        }
    }

    showErrorDialog?.let { msg ->
        BasicAlertDialog(
            title = msg,
            onComplete = { showErrorDialog = null },
        )
    }

    if (showCompleteDialog) {
        BasicAlertDialog(
            title = stringResource(id = R.string.signup_msg_complete),
            onComplete = { moveToLogin() }
        )
    }

    when (uiState) {
        SignUpUiState.Idle -> {}
        SignUpUiState.Loading -> {}
        SignUpUiState.Success -> {
            showCompleteDialog = true
        }
    }

    SignUpScreen(
        uiState = uiState,
        formState = formState,
        isEmailValid = isEmailValid,
        isPasswordValid = isPasswordValid,
        isPasswordMatch = isPasswordMatch,
        isFormValid = isFormValid,
        onChangeEmail = { vm.updateEmail(it) },
        onChangePassword = { vm.updatePassword(it) },
        onChangePasswordConfirm = { vm.updatePasswordConfirm(it) },
        onClickSignUp = { vm.createAccountWithEmail(context) },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun SignUpScreen(
    modifier: Modifier = Modifier,
    uiState: SignUpUiState,
    formState: SignUpFormState,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    isPasswordMatch: Boolean,
    isFormValid: Boolean,
    onChangeEmail: (String) -> Unit,
    onChangePassword: (String) -> Unit,
    onChangePasswordConfirm: (String) -> Unit,
    onClickSignUp: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    
    Scaffold(
        topBar = {
            SentyCenterAlignedTopAppBar(
                title = R.string.signup_title,
                onBackPressed = onBackPressed,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SentyWhite)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() },
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
                    .padding(bottom = 88.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EmailSection(
                    email = formState.email,
                    isEmailValid = isEmailValid,
                    onChangeEmail = onChangeEmail,
                )

                Spacer(modifier = Modifier.height(32.dp))

                PasswordSection(
                    password = formState.password,
                    passwordConfirm = formState.passwordConfirm,
                    isPasswordValid = isPasswordValid,
                    isPasswordMatch = isPasswordMatch,
                    onChangePassword = onChangePassword,
                    onChangePasswordConfirm = onChangePasswordConfirm,
                )
            }

            SentyFilledButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                text = stringResource(id = R.string.common_confirm),
                enabled = isFormValid,
                onClick = onClickSignUp,
            )
        }
    }

    if (uiState is SignUpUiState.Loading) {
        LoadingCircleIndicator()
    }
}

@Composable
private fun EmailSection(
    modifier: Modifier = Modifier,
    email: String,
    isEmailValid: Boolean,
    onChangeEmail: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.signup_email_title),
            style = SentyTheme.typography.headlineSmall
                .copy(color = SentyBlack),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.signup_email_title_sub1),
            style = SentyTheme.typography.bodySmall
                .copy(color = SentyGray60),
        )
        Text(
            text = stringResource(id = R.string.signup_email_title_sub2),
            style = SentyTheme.typography.bodySmall
                .copy(color = SentyGray60),
        )
        Spacer(modifier = Modifier.height(4.dp))

        SentyEmailTextField(
            modifier = Modifier.fillMaxWidth(),
            text = email,
            hint = stringResource(id = R.string.signup_email_hint_text),
            onChangeText = onChangeEmail,
            isError = if (email.isNotEmpty()) !isEmailValid else false,
            errorMsg = stringResource(id = R.string.signup_error_email)
        )
    }
}

@Composable
private fun PasswordSection(
    modifier: Modifier = Modifier,
    isPasswordValid: Boolean,
    isPasswordMatch: Boolean,
    password: String,
    passwordConfirm: String,
    onChangePassword: (String) -> Unit,
    onChangePasswordConfirm: (String) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(id = R.string.signup_password_title),
            style = SentyTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.signup_password_title_sub1),
            style = SentyTheme.typography.bodySmall
                .copy(color = SentyGray60),
        )
        Spacer(modifier = Modifier.height(4.dp))

        SentyPasswordTextField(
            password = password,
            onChangeText = onChangePassword,
            modifier = Modifier.fillMaxWidth(),
            hint = stringResource(id = R.string.signup_password_hint_text),
            isError = if (password.isNotEmpty()) !isPasswordValid else false,
            errorMsg = stringResource(id = R.string.signup_error_password),
        )

        SentyPasswordTextField(
            password = passwordConfirm,
            onChangeText = { onChangePasswordConfirm(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            hint = stringResource(id = R.string.signup_password_hint_text),
            isError = if (password.isNotEmpty() && passwordConfirm.isNotEmpty()) !isPasswordMatch else false,
            errorMsg = stringResource(id = R.string.signup_error_password_confirm),
        )
    }
}

@Preview(showBackground = true, widthDp = 375)
@Composable
private fun SignupScreenPreview() {
    SentyTheme {
        SignUpScreen(
            uiState = SignUpUiState.Loading,
            formState = SignUpFormState(),
            isEmailValid = false,
            isPasswordValid = false,
            isPasswordMatch = false,
            isFormValid = true,
            onChangeEmail = {},
            onChangePassword = {},
            onChangePasswordConfirm = {},
            onClickSignUp = {},
            onBackPressed = {},
        )
    }
}