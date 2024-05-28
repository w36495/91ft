package com.w36495.senty.view.screen.anniversary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.ui.component.dialogs.BasicCalendarDialog
import com.w36495.senty.view.ui.component.dialogs.BasicTimePickerDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnniversaryBottomSheetDialog(
    type: AnniversaryDialogType = AnniversaryDialogType.ADD,
    schedule: Schedule? = null,
    selectDate: List<Int>,
    onDismiss: () -> Unit,
    onClickEdit: ((Schedule) -> Unit)? = null,
    onClickSave: ((Schedule) -> Unit)? = null,
    onClickDelete: ((String) -> Unit)? = null,
) {
    var currentType by remember { mutableStateOf(type) }
    var showCalendar by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showMap by remember { mutableStateOf(false) }

    var selectYear by remember { mutableIntStateOf(selectDate[0]) }
    var selectMonth by remember { mutableIntStateOf(selectDate[1]) }
    var selectDay by remember { mutableIntStateOf(selectDate[2]) }
    var title by remember { mutableStateOf(schedule?.title ?: "") }
    var time by remember { mutableStateOf(schedule?.time ?: "시간을 입력해주세요.") }
    var memo by remember { mutableStateOf(schedule?.memo ?: "") }
    var location by remember { mutableStateOf(schedule?.location ?: "장소를 입력해주세요.") }

    if (showCalendar) {
        BasicCalendarDialog(
            onDismiss = { showCalendar = false },
            onSelectedDate = { y, m, d ->
                selectYear = y
                selectMonth = m + 1
                selectDay = d
            }
        )
    }
    if (showTimePicker) {
        BasicTimePickerDialog(onDismiss = { showTimePicker = false }) { hour, minute ->
            time = "${StringUtils.format2Digits(hour)}:${StringUtils.format2Digits(minute)}"
        }
    }
    if (showMap) {
        ScheduleMapScreen(
            onBackPressed = { showMap = false },
            onSelectLocation = { address ->
                location = address.address
                showMap = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        CenterAlignedTopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { onDismiss() }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            },
            actions = {
                if (currentType == AnniversaryDialogType.READ) {
                    IconButton(onClick = {
                        if (onClickDelete != null) {
                            onClickDelete(schedule!!.id)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                IconButton(
                    onClick = {
                        if (currentType == AnniversaryDialogType.READ) {
                            currentType = AnniversaryDialogType.ADD
                        } else {
                            val newSchedule = Schedule(
                                title = title,
                                date = "${selectYear}-${StringUtils.format2Digits(selectMonth)}-${
                                    StringUtils.format2Digits(
                                        selectDay
                                    )
                                }",
                                time = if (time == "시간을 입력해주세요.") "" else time,
                                location = if (location == "장소를 입력해주세요.") "" else location,
                                memo = memo
                            )

                            if (schedule == null) {
                                if (onClickSave != null) {
                                    onClickSave(newSchedule)
                                }
                            } else {
                                newSchedule.setId(schedule.id)
                                if (onClickEdit != null) {
                                    onClickEdit(newSchedule)
                                }
                            }

                            title = ""
                            time = "시간을 입력해주세요."
                            memo = ""
                            location = "장소를 입력해주세요."

                            onDismiss()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentType == AnniversaryDialogType.ADD) Icons.Default.Check else Icons.Default.Edit,
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (currentType == AnniversaryDialogType.ADD)
                Text(text = "날짜", style = MaterialTheme.typography.bodyMedium)
            else Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${selectYear}년 ${selectMonth}월 ${selectDay}일",
                    style = MaterialTheme.typography.headlineSmall,
                    fontSize = 24.sp
                )
                if (currentType == AnniversaryDialogType.ADD) {
                    IconButton(onClick = { showCalendar = true }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit, contentDescription = null,
                            tint = Color(0xFF848484)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentType == AnniversaryDialogType.ADD) {
                Text(text = "제목", style = MaterialTheme.typography.bodyMedium)

                SentyTextField(
                    text = title, hint = "제목을 입력하세요.", errorMsg = "",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    title = it
                }
            } else {
                SentyReadOnlyTextField(text = schedule!!.title, textColor = Color.Black)
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "장소", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (location == "") "장소를 입력해주세요." else location,
                    modifier = Modifier.clickable {
                        showMap = true
                    }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "시간", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = if (time == "") "시간을 입력해주세요." else time,
                    modifier = Modifier.clickable { showTimePicker = true }
                )
            }
            Text(
                text = "메모", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            SentyMultipleTextField(text = memo, onChangeText = {
                memo = it
            }, readOnly = currentType == AnniversaryDialogType.READ)
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

enum class AnniversaryDialogType {
    ADD, READ
}