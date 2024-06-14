package com.w36495.senty.view.screen.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.viewModel.ResetPasswordViewModel
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.textFields.SentyEmailTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindPasswordDialogScreen(
    vm: ResetPasswordViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    val hasEmailError by vm.hasEmailError.collectAsState()
    val emailErrorMsg by vm.emailErrorMsg.collectAsState()
    val result by vm.result.collectAsState()

    if (result) onDismiss()

    var email by rememberSaveable { mutableStateOf("") }

    BasicAlertDialog(
        onDismissRequest = { onDismiss() },
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    title = { Text(text = "비밀번호 재설정") })
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "이메일",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    SentyEmailTextField(
                        text = email,
                        hint = "",
                        isError = hasEmailError,
                        errorMsg = emailErrorMsg,
                        onChangeText = {
                            email = it
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                SentyFilledButton(
                    text = "확인",
                    onClick = { vm.sendPasswordResetEmail(email) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                SentyOutlinedButton(
                    text = "취소",
                    onClick = { onDismiss() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}