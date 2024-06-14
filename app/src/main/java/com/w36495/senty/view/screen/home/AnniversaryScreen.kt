package com.w36495.senty.view.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vsnappy1.datepicker.DatePicker
import com.vsnappy1.datepicker.data.model.DatePickerDate
import com.vsnappy1.datepicker.data.model.SelectionLimiter
import com.vsnappy1.datepicker.ui.model.DatePickerConfiguration
import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.screen.anniversary.AnniversaryBottomSheetDialog
import com.w36495.senty.view.screen.anniversary.AnniversaryDialogType
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.cards.ScheduleCard
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.AnniversaryViewModel

@Composable
fun AnniversaryScreen(
    vm: AnniversaryViewModel = hiltViewModel(),
) {
    val schedules by vm.schedules.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        vm.snackMsg.collect{
            snackbarHostState.showSnackbar(it)
        }
    }

    AnniversaryScreenContents(
        schedules = schedules,
        snackbarHostState = snackbarHostState,
        onClickDate = { year, month, day ->
            vm.getSchedules(year, month, day)
        },
        onClickSave = { vm.saveSchedule(it) },
        onClickEdit = { vm.updateSchedule(it) },
        onClickDelete = { vm.removeSchedule(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun AnniversaryScreenContents(
    schedules: List<Schedule>,
    snackbarHostState: SnackbarHostState,
    onClickDate: (Int, Int, Int) -> Unit,
    onClickSave: (Schedule) -> Unit,
    onClickEdit: (Schedule) -> Unit,
    onClickDelete: (String) -> Unit,
) {
    val scaffoldState = androidx.compose.material.rememberBottomSheetScaffoldState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showReadDialog by remember { mutableStateOf(false) }
    var savedSchedule by remember { mutableStateOf(Schedule.emptySchedule) }
    val currentDate = DateUtil.getCurrentDate().map { it.toInt() }
    var year by remember { mutableIntStateOf(currentDate[0]) }
    var month by remember { mutableIntStateOf(currentDate[1]) }
    var day by remember { mutableIntStateOf(currentDate[2]) }

    LaunchedEffect(showAddDialog || showReadDialog) {
        if (showAddDialog || showReadDialog) scaffoldState.bottomSheetState.expand()
        else scaffoldState.bottomSheetState.collapse()
    }

    BottomSheetScaffold(
        sheetContent = {
            if (showAddDialog) {
                AnniversaryBottomSheetDialog(
                    selectDate = listOf(year, month, day),
                    onDismiss = { showAddDialog = false },
                    onComplete = { showAddDialog = false },
                )
            } else if (showReadDialog) {
                val savedDate = savedSchedule.date.split("-").map { it.toInt() }

                AnniversaryBottomSheetDialog(
                    type = AnniversaryDialogType.READ,
                    schedule = savedSchedule,
                    selectDate = savedDate,
                    onDismiss = {
                        showReadDialog = false
                    },
                    onClickDelete = { onClickDelete(it) },
                    onComplete = { showReadDialog = false }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
            ) { showAddDialog = true }

            BottomScheduleSection(
                schedules = schedules,
                modifier = Modifier.fillMaxWidth(),
                onClickSchedule = { schedule ->
                    savedSchedule = schedule
                    showReadDialog = true
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
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        schedules.forEachIndexed { index, schedule ->
            ScheduleCard(
                schedule = schedule,
                onClickSchedule = { onClickSchedule(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}