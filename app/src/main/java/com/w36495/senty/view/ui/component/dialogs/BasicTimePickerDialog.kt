package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.vsnappy1.timepicker.TimePicker
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTimePickerDialog(
    onDismiss: () -> Unit,
    onSelectTime: (Int, Int) -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "시간선택") },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )

                TimePicker(
                    onTimeSelected = { hour, minute ->
                        onSelectTime(hour, minute)
                    },
                    is24Hour = true,
                    modifier = Modifier.background(Color.White)
                )

                SentyFilledButton(
                    text = "저장",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    onDismiss()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TimePickerDialogPreview() {
    SentyTheme {
        BasicTimePickerDialog(
            onDismiss = {},
            onSelectTime = { _, _ -> }
        )
    }
}