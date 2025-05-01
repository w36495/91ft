package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.R
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun BasicAlertDialog(
    title: String? = null,
    message: String? = null,
    hasCancel: Boolean = false,
    onComplete: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = SentyWhite,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                title?.let {
                    Text(
                        text = it,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = SentyTheme.typography.titleMedium
                            .copy(color = SentyBlack),
                    )
                }

                message?.let {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        textAlign = TextAlign.Center,
                        style = SentyTheme.typography.bodyMedium
                            .copy(color = SentyBlack, lineHeight = 22.sp),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp)
                ) {
                    if (hasCancel) {
                        SentyOutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            text = stringResource(id = R.string.common_cancel),
                            onClick = onDismiss
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    SentyFilledButton(
                        text = "확인",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        onComplete()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BasicAlertDialogPreview() {
    SentyTheme {
        BasicAlertDialog(
            title = "로그아웃 하시겠습니까?",
            hasCancel = true,
//            message = "해당 그룹으로 설정되어있는 친구들도 모두 함께 삭제됩니다. 삭제된 그룹은 복구가 불가능합니다.",
            onDismiss = {},
            onComplete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BasicAlertDialogOnlyConfirmPreview() {
    SentyTheme {
        BasicAlertDialog(
            title = "로그아웃 하시겠습니까?",
//            message = "해당 그룹으로 설정되어있는 친구들도 모두 함께 삭제됩니다. 삭제된 그룹은 복구가 불가능합니다.",
            onDismiss = {},
            onComplete = {},
        )
    }
}