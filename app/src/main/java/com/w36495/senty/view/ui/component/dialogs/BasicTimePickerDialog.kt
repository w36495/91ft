package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.R
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.theme.SentyGreen60

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicTimePickerDialog(
    onSelectTime: (Int, Int) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        is24Hour = true,
    )

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "시간 선택",
                            style = SentyTheme.typography.headlineSmall,
                        )
                    },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                TimeInput(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        periodSelectorSelectedContainerColor = SentyGreen60.copy(0.3f),
                        timeSelectorSelectedContainerColor = MaterialTheme.colorScheme.onPrimary,
                        timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.onSurface.copy(
                            alpha = 0.1f
                        ),
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.padding(bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SentyOutlinedButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                        text = stringResource(id = R.string.common_reset),
                        onClick = { onReset() },
                    )

                    SentyFilledButton(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        text = stringResource(id = R.string.common_confirm),
                        onClick = {
                            onSelectTime(timePickerState.hour, timePickerState.minute)
                            onDismiss()
                        },
                    )
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
            onReset = {},
            onSelectTime = { _, _ -> }
        )
    }
}