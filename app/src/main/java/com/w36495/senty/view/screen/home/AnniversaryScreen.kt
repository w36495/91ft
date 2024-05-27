package com.w36495.senty.view.screen.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.datepicker.data.model.SelectionLimiter
import com.vsnappy1.datepicker.ui.model.DatePickerConfiguration
import com.w36495.senty.util.DateUtil
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.cards.ScheduleCard
import com.w36495.senty.view.ui.component.dialogs.BasicCalendarDialog
import com.w36495.senty.view.ui.component.dialogs.BasicTimePickerDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.AnniversaryViewModel
import kotlinx.coroutines.launch

@Composable
fun AnniversaryScreen(
    vm: AnniversaryViewModel = hiltViewModel()
) {
    val schedules by vm.schedules.collectAsState()

    AnniversaryScreenContents(
        schedules = schedules,
        onClickDate = { year, month, day ->
            vm.getSchedules(year, month, day)
        },
        onClickSave = { vm.saveSchedule(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun AnniversaryScreenContents(
    schedules: List<Schedule>,
    onClickDate: (Int, Int, Int) -> Unit,
    onClickSave: (Schedule) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = androidx.compose.material.rememberBottomSheetScaffoldState()

    val currentDate = DateUtil.getCurrentDate().map { it.toInt() }
    var year by remember { mutableIntStateOf(currentDate[0]) }
    var month by remember { mutableIntStateOf(currentDate[1]) }
    var day by remember { mutableIntStateOf(currentDate[2]) }

    BottomSheetScaffold(
        sheetContent = {
            AnniversaryBottomSheetContents(
                selectDate = listOf(year, month, day),
                onDismiss = { scope.launch { scaffoldState.bottomSheetState.collapse() } },
                onClickSave = { onClickSave(it) }
            )
        },
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "기념일") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color(0xFFFBFBFB))
                .verticalScroll(rememberScrollState())
        ) {
            TopCalendarSection(
                modifier = Modifier.fillMaxWidth(),
                onClickDate = { y, m, d ->
                    year = y
                    month = m
                    day = d

                    onClickDate(y, m, d)
                }
            )

            SentyOutlinedButton(
                text = "기념일 등록하기",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .background(Color.White)
            ) {
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }

            BottomScheduleSection(
                schedules = schedules,
                modifier = Modifier.fillMaxWidth(),
                onClickSchedule = {

                }
            )
        }
    }
}

@Composable
private fun TopCalendarSection(
    modifier: Modifier = Modifier,
    onClickDate: (Int, Int, Int) -> Unit,
) {
    val (year, month, day) = DateUtil.getCurrentDate().map { it.toInt() }

    Box(modifier = modifier.background(Color.White)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            DatePicker(
                onDateSelected = { year, month, day ->
                    onClickDate(year, month + 1, day)
                },
                selectionLimiter = SelectionLimiter(
                    fromDate = DatePickerDate(2000, 1, 1)
                ),
                date = DatePickerDate(year, month - 1, day),
                configuration = DatePickerConfiguration.Builder()
                    .selectedDateBackgroundColor(Green40)
                    .build()
            )
        }
    }
}

@Composable
private fun BottomScheduleSection(
    modifier: Modifier = Modifier,
    schedules: List<Schedule>,
    onClickSchedule: (Schedule) -> Unit,
) {
    Column(modifier = modifier
        .padding(horizontal = 16.dp)
        .padding(top = 16.dp)) {
        schedules.forEachIndexed { index, schedule ->
            ScheduleCard(schedule = schedule) { onClickSchedule(it) }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnniversaryBottomSheetContents(
    selectDate: List<Int>,
    onDismiss: () -> Unit,
    onClickSave: (Schedule) -> Unit,
) {
    var showCalendar by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var selectYear by remember { mutableIntStateOf(selectDate[0]) }
    var selectMonth by remember { mutableIntStateOf(selectDate[1]) }
    var selectDay by remember { mutableIntStateOf(selectDate[2]) }
    var title by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("시간을 입력해주세요.") }
    var memo by remember { mutableStateOf("") }

    if (showCalendar) {
        BasicCalendarDialog(
            onDismiss = { showCalendar = false },
            onSelectedDate = { y, m, d ->
                selectYear = y
                selectMonth = m+1
                selectDay = d
            }
        )
    }
    if (showTimePicker) {
        BasicTimePickerDialog(onDismiss = { showTimePicker = false }) { hour, minute ->
            time = "${StringUtils.format2Digits(hour)}:${StringUtils.format2Digits(minute)}"
        }
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
                IconButton(
                    onClick = {
                        onClickSave(
                            Schedule(
                                title = title,
                                date = "${selectYear}-${StringUtils.format2Digits(selectMonth)}-${StringUtils.format2Digits(selectDay)}",
                                time = if (time == "시간을 입력해주세요.") "" else time,
                                location = "",
                                memo = memo
                            )
                        )

                        onDismiss()
                    }
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "날짜", style = MaterialTheme.typography.bodyMedium)

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
                IconButton(onClick = { showCalendar = true }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit, contentDescription = null,
                        tint = Color(0xFF848484)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "제목", style = MaterialTheme.typography.bodyMedium)
            SentyTextField(
                text = title, hint = "제목을 입력하세요.", errorMsg = "",
                modifier = Modifier.fillMaxWidth()
            ) {
                title = it
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "장소", style = MaterialTheme.typography.bodyMedium)
                Text(text = "장소를 입력해주세요.", color = Color(0xFF848484))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "시간", style = MaterialTheme.typography.bodyMedium)
                Text(text = time, color = Color(0xFF848484),
                    modifier = Modifier.clickable { showTimePicker = true })
            }
            Text(
                text = "메모", style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            SentyMultipleTextField(text = memo, onChangeText = {
                memo = it
            })
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}