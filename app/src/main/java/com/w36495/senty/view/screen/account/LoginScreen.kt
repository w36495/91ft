package com.w36495.senty.view.screen.account

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.w36495.senty.R
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginScreen(
    vm: LoginViewModel = hiltViewModel(),
    onSuccessLogin: () -> Unit,
    onClickSignUp: () -> Unit,
) {
    val loginResult by vm.result.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val credentials = vm.signInClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken

                    googleIdToken?.let { token ->
                        vm.signInWithGoogle(token)
                    }
                }
            }
        }

    LaunchedEffect(true) {
        vm.errorFlow.collectLatest {
            snackBarHostState.showSnackbar(
                message = it,
            )
        }
    }

    if (loginResult || vm.autoLogin.value) onSuccessLogin()

    LoginContents(
        loading = vm.loading.value,
        sendSnackbarMessage = { vm.sendSnackbarMessage(it) },
        snackBarHostState = snackBarHostState,
        onClickLogin = { email, password, checkedAutoLogin ->
            vm.userLogin(email, password, checkedAutoLogin)
        },
        onClickSignUp = { onClickSignUp() },
        onFacebookLoginClick = { vm.signInWithFacebook(it) },
        onGoogleLoginClick = {
            vm.signInClient.signOut()
            vm.signInClient.beginSignIn(vm.signInRequest)
                .addOnSuccessListener { result ->
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(result.pendingIntent.intentSender)
                        .build()
                    googleAuthLauncher.launch(intentSenderRequest)
                }
        }
    )
}

@Composable
private fun LoginContents(
    loading: Boolean,
    snackBarHostState: SnackbarHostState,
    sendSnackbarMessage: (String) -> Unit,
    onFacebookLoginClick: (String) -> Unit,
    onGoogleLoginClick: () -> Unit,
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

                Spacer(modifier = Modifier.height(32.dp))

                SocialLogins(
                    modifier = Modifier.fillMaxWidth(),
                    sendSnackbarMessage = sendSnackbarMessage,
                    onGoogleLoginClick = onGoogleLoginClick,
                    onFacebookLoginClick = onFacebookLoginClick,
                )
            }

            if (loading) {
                LoadingProgressIndicator(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun LoadingProgressIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = Green40)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "로그인을 진행하고 있습니다.",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SocialLogins(
    modifier: Modifier = Modifier,
    sendSnackbarMessage: (String) -> Unit,
    onFacebookLoginClick: (String) -> Unit,
    onGoogleLoginClick: () -> Unit,
) {
    val facebookLoginManager = LoginManager.getInstance()
    val callbackManager = remember { CallbackManager.Factory.create() }
    val launcher = rememberLauncherForActivityResult(
        facebookLoginManager.createLogInActivityResultContract(callbackManager, null)) {
    }

    DisposableEffect(Unit) {
        facebookLoginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                sendSnackbarMessage(error.stackTraceToString())
            }

            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                onFacebookLoginClick(token)
            }
        })
        onDispose {
            facebookLoginManager.unregisterCallback(callbackManager)
        }
    }

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
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
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
                painter = painterResource(
                    id = R.drawable.login_symbol_facebook
                ),
                contentDescription = "Facebook Login Button",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { launcher.launch(listOf("email", "public_profile")) }
            )

            Spacer(modifier = Modifier.width(32.dp))

            Image(
                painter = painterResource(
                    id = R.drawable.login_symbol_google
                ),
                contentDescription = "Google Login Button",
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onGoogleLoginClick() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialLoginsPreview() {
    SentyTheme {
        SocialLogins(
            sendSnackbarMessage = {},
            onFacebookLoginClick = {},
            onGoogleLoginClick = {}
        )
    }
}