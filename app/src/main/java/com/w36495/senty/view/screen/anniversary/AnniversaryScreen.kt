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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.util.DateUtil
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.screen.anniversary.AnniversaryBottomSheetDialog
import com.w36495.senty.view.screen.anniversary.AnniversaryCalendar
import com.w36495.senty.view.screen.anniversary.AnniversaryDialogType
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.cards.ScheduleCard
import com.w36495.senty.viewModel.AnniversaryViewModel

@Composable
fun AnniversaryRoute(
    vm: AnniversaryViewModel = hiltViewModel(),
    padding: PaddingValues,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val schedules by vm.schedules.collectAsStateWithLifecycle()
    val schedulesOfSelectionDate by vm.schedulesOfSelectionDate.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        vm.snackMsg.collect{
            snackbarHostState.showSnackbar(it)
        }
    }

    AnniversaryScreen(
        schedules = schedules,
        schedulesOfSelectionDate = schedulesOfSelectionDate,
        snackbarHostState = snackbarHostState,
        onClickDate = { year, month, day ->
            vm.getSchedules(year, month, day)
        },
        onClickDelete = { vm.removeSchedule(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun AnniversaryScreen(
    schedules: List<Schedule>,
    schedulesOfSelectionDate: List<Schedule>,
    snackbarHostState: SnackbarHostState,
    onClickDate: (Int, Int, Int) -> Unit,
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
                schedules = schedules,
                onClickDate = { y, m, d ->
                    year = y
                    month = m
                    day = d

                    onClickDate(y, m, d)
                }
            )

            SentyOutlinedButton(
                text = "새로운 기념일 등록하기",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
                    .background(Color.White)
            ) { showAddDialog = true }

            BottomScheduleSection(
                schedules = schedulesOfSelectionDate,
                modifier = Modifier.fillMaxWidth(),
                year = year,
                month = month,
                day = day,
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
    schedules: List<Schedule>,
    onClickDate: (Int, Int, Int) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(bottom = 8.dp)
            .background(Color.White)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            AnniversaryCalendar(
                schedules = schedules,
                onSelectedDate = { year, month, day ->
                    onClickDate(year, month, day)
                }
            )
        }
    }
}

@Composable
private fun BottomScheduleSection(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    day: Int,
    schedules: List<Schedule>,
    onClickSchedule: (Schedule) -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "${year}년 ${month}월 ${day}일",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        schedules.forEachIndexed { index, schedule ->
            ScheduleCard(
                schedule = schedule,
                onClickSchedule = { onClickSchedule(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}