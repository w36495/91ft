package com.w36495.senty.view.screen.anniversary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.anniversary.contact.AnniversaryContact
import com.w36495.senty.view.screen.anniversary.edit.AnniversaryBottomSheet
import com.w36495.senty.view.screen.anniversary.edit.AnniversaryBottomSheetType
import com.w36495.senty.view.screen.anniversary.model.ScheduleUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.cards.ScheduleCard
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGray80
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.SentyYellow60
import com.w36495.senty.viewModel.AnniversaryViewModel

@Composable
fun AnniversaryRoute(
    vm: AnniversaryViewModel = hiltViewModel(),
    padding: PaddingValues,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val uiState by vm.state.collectAsStateWithLifecycle()

    AnniversaryScreen(
        modifier = Modifier.padding(bottom = padding.calculateBottomPadding()),
        uiState = uiState,
        onSelectDate = { year, month, day ->
            vm.handleEvent(AnniversaryContact.Event.UpdateSelectedDate(year, month, day))
        },
        onClickSchedule = { vm.handleEvent(AnniversaryContact.Event.OnClickSchedule(it)) },
        onClickAddSchedule = { vm.handleEvent(AnniversaryContact.Event.OnClickAddSchedule) },
        onClickCloseScheduleBottomSheet = { vm.handleEvent(AnniversaryContact.Event.OnClickCloseScheduleBottomSheet) },
        onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnniversaryScreen(
    modifier: Modifier = Modifier,
    uiState: AnniversaryContact.State,
    onSelectDate: (Int, Int, Int) -> Unit,
    onClickAddSchedule: () -> Unit,
    onClickSchedule: (ScheduleUiModel) -> Unit,
    onClickCloseScheduleBottomSheet: () -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    var bottomSheetType by remember { mutableStateOf(AnniversaryBottomSheetType.READ) }
    val scaffoldState = rememberScaffoldState()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    if (uiState.showBottomSheet) {
        AnniversaryBottomSheet(
            sheetState = bottomSheetState,
            scheduleId = uiState.selectedSchedule?.id,
            type = bottomSheetType,
            selectedDate = uiState.selectedDate,
            onShowGlobalErrorSnackBar = onShowGlobalErrorSnackBar,
            onChangeBottomSheetType = { bottomSheetType = AnniversaryBottomSheetType.EDIT },
            onDismiss = {
                bottomSheetType = AnniversaryBottomSheetType.READ
                onClickCloseScheduleBottomSheet()
            },
        )
    }

    Scaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.anniversary_title),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SentyWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBFBFB))
                    .verticalScroll(rememberScrollState())
            ) {
                TopCalendarSection(
                    modifier = Modifier.fillMaxWidth(),
                    schedules = uiState.schedules,
                    onClickDate = { year, month, day -> onSelectDate(year, month, day) },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                ) {
                    ScheduleLabel(
                        chipColor = SentyGreen60,
                        text = stringResource(id = R.string.anniversary_label_today_text)
                    )

                    ScheduleLabel(
                        chipColor = SentyYellow60,
                        text = stringResource(id = R.string.anniversary_label_schedule_text)
                    )
                }

                SentyOutlinedButton(
                    text = stringResource(id = R.string.anniversary_button_add_text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                        .background(Color.White),
                    onClick = {
                        bottomSheetType = AnniversaryBottomSheetType.EDIT
                        onClickAddSchedule()
                    },
                )

                BottomScheduleSection(
                    schedules = uiState.selectedSchedules,
                    year = uiState.selectedDate.first,
                    month = uiState.selectedDate.second,
                    day = uiState.selectedDate.third,
                    onClickSchedule = { schedule ->
                        bottomSheetType = AnniversaryBottomSheetType.READ
                        onClickSchedule(schedule)
                    },
                )
            }
        }
    }
}

@Composable
private fun ScheduleLabel(
    chipColor: Color,
    text: String,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = chipColor,
                    shape = CircleShape,
                )
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp),
            style = SentyTheme.typography.labelSmall,
        )
    }
}
@Composable
private fun TopCalendarSection(
    modifier: Modifier = Modifier,
    schedules: List<ScheduleUiModel>,
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
    schedules: List<ScheduleUiModel>,
    onClickSchedule: (ScheduleUiModel) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "${year}년 ${month}월 ${day}일",
            style = SentyTheme.typography.titleMedium
                .copy(fontWeight = FontWeight.Bold),
            color = SentyGray80,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.anniversary_empty_text),
                    style = SentyTheme.typography.labelSmall
                        .copy(color = SentyGray60),
                )
            }
        } else {
            schedules.forEachIndexed { index, schedule ->
                ScheduleCard(
                    schedule = schedule,
                    onClickSchedule = { onClickSchedule(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}