package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.view.ui.component.buttons.SentyElevatedButton
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@Composable
fun FriendDeleteDialogContents(
    modifier: Modifier = Modifier,
    onClickDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                TopAppBar(
                    title = { Text(text = "친구를 삭제하시겠습니까?") },
                    backgroundColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = 0.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "친구와 주고받았던 선물들도 모두 함께 삭제됩니다.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "삭제된 친구/선물에 대한 정보는 복원이 불가능합니다.",
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 4.dp),
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = "선택한 친구의 정보를 삭제하시겠습니까?",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(32.dp))

                SentyFilledButton(
                    text = "삭제",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { onClickDelete() },
                    buttonColor = MaterialTheme.colorScheme.error
                )
                SentyElevatedButton(
                    text = "취소",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 16.dp),
                    onClick = { onDismiss() }
                )
            }
        }
    }
}