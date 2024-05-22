package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.datepicker.ui.model.DatePickerConfiguration
import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.Green40

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicCalendarDialog(
    onDismiss: () -> Unit,
    onSelectedDate: (Int, Int, Int) -> Unit,
) {
    val (year, month, day) = DateUtil.getCurrentDate().map { it.toInt() }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "날짜선택") },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )
                DatePicker(
                    modifier = Modifier
                        .padding(16.dp),
                    onDateSelected = { year, month, day ->
                        onSelectedDate(year, month, day)
                    },
                    date = DatePickerDate(year, month-1, day),
                    configuration = DatePickerConfiguration.Builder()
                        .selectedDateBackgroundColor(Green40)
                        .build()
                )

                SentyFilledButton(
                    text = "저장",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                ) {
                    onDismiss()
                }
            }

        }
    }

}

@Preview(showBackground = true)
@Composable
private fun BasicCalendarPreview() {
    SentyTheme {
        BasicCalendarDialog(
            onDismiss = {},
            onSelectedDate = { year, month, day ->

            }
        )
    }
}