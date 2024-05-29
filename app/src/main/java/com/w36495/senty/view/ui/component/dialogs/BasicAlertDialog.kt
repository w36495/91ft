package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@Composable
fun BasicAlertDialog(
    title: String,
    discContent: (@Composable () -> Unit)? = null,
    onComplete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                if (discContent != null) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)) {
                        discContent()
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    SentyFilledButton(
                        text = "취소",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        buttonColor = Color(0xFFFBFBFB),
                        textColor = Color.Black
                    ) {
                        onDismiss()
                    }

                    Spacer(modifier = Modifier.width(8.dp))
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
private fun BasicAlertDialogPreview(

) {
    SentyTheme {
        BasicAlertDialog(title = "로그아웃 하시겠습니까?",
            discContent = {
                Text(
                    text = "해당 그룹으로 설정되어있는 친구들도 모두 함께 삭제됩니다. 삭제된 그룹은 복구가 불가능합니다.",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
            }, onComplete = { /*TODO*/ }) {

        }
    }
}