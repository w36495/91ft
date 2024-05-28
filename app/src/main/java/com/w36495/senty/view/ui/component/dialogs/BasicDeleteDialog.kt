package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyElevatedButton
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@Composable
fun BasicDeleteDialog(
    modifier: Modifier = Modifier,
    title: String,
    discContents: @Composable () -> Unit,
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
                    title = { Text(text = title) },
                    backgroundColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = 0.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                ) { discContents() }

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

@Preview(showBackground = true)
@Composable
private fun BasicDeleteDialogPreview() {
    SentyTheme {
        BasicDeleteDialog(
            title = "제목",
            discContents = {
                Text(
                    text = "내용",
                    fontWeight = FontWeight.Bold,
                )
            },
            onClickDelete = {},
            onDismiss = {}
        )
    }
}