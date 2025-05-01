package com.w36495.senty.view.screen.anniversary.edit

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.screen.anniversary.contact.AnniversaryBottomSheetContact
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButtonWithProgress
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.dialogs.BasicCalendarDialog
import com.w36495.senty.view.ui.component.dialogs.BasicTimePickerDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray50
import com.w36495.senty.view.ui.theme.SentyWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnniversaryBottomSheet(
    vm: AnniversaryBottomSheetViewModel = hiltViewModel(),
    scheduleId: String? = null,
    type: AnniversaryBottomSheetType,
    sheetState: SheetState,
    selectedDate: Triple<Int, Int, Int>,
    onChangeBottomSheetType: (AnniversaryBottomSheetType) -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    LaunchedEffect(scheduleId) {
        scheduleId?.let {
            vm.getSchedule(it)
        }
    }

    LaunchedEffect(Unit) {
        vm.handleEvent(AnniversaryBottomSheetContact.Event.UpdateDate(selectedDate))
    }

    LaunchedEffect(true) {
        vm.effect.collect { effect ->
            when (effect) {
                is AnniversaryBottomSheetContact.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
                is AnniversaryBottomSheetContact.Effect.ShowError -> {
                    onShowGlobalErrorSnackBar(effect.throwable)
                }
            }
        }
    }

    val uiState by vm.state.collectAsStateWithLifecycle()

    when {
        uiState.showCalendar -> {
            BasicCalendarDialog(
                onDismiss = { vm.handleEvent(AnniversaryBottomSheetContact.Event.HideCalendar) },
                onSelectedDate = { y, m, d ->
                    vm.handleEvent(AnniversaryBottomSheetContact.Event.UpdateDate(Triple(y, m+1, d)))
                },
            )
        }
        uiState.showTimePicker -> {
            BasicTimePickerDialog(
                onDismiss = { vm.handleEvent(AnniversaryBottomSheetContact.Event.HideTimePicker) },
                onReset = { vm.handleEvent(AnniversaryBottomSheetContact.Event.ResetTime) },
                onSelectTime = { hour, minute ->
                    val time = "${StringUtils.format2Digits(hour)}:${StringUtils.format2Digits(minute)}"
                    vm.handleEvent(AnniversaryBottomSheetContact.Event.UpdateTime(time))
                },
            )
        }
        uiState.showDeleteDialog -> {
            BasicAlertDialog(
                title = stringResource(id = R.string.anniversary_delete_title),
                message = stringResource(id = R.string.anniversary_delete_message_text),
                hasCancel = true,
                onDismiss = { vm.handleEvent(AnniversaryBottomSheetContact.Event.HideDeleteDialog) },
                onComplete = { vm.handleEvent(AnniversaryBottomSheetContact.Event.OnClickDelete) },
            )
        }
    }

    AnniversaryBottomSheetContents(
        uiState = uiState,
        scheduleId = scheduleId,
        sheetState = sheetState,
        type = type,
        onDismiss = {
            vm.clearState()
            onDismiss()
        },
        onClickEvent = { vm.handleEvent(it) },
        onChangeBottomSheetType = onChangeBottomSheetType,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnniversaryBottomSheetContents(
    uiState: AnniversaryBottomSheetContact.State,
    type: AnniversaryBottomSheetType,
    scheduleId: String?,
    sheetState: SheetState,
    onClickEvent: (AnniversaryBottomSheetContact.Event) -> Unit,
    onChangeBottomSheetType: (AnniversaryBottomSheetType) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        dragHandle = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(SentyWhite)
            )
        },
        sheetState = sheetState,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(Color.White)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { focusManager.clearFocus() }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                CenterAlignedTopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    actions = {
                        if (type == AnniversaryBottomSheetType.READ) {
                            IconButton(onClick = {
                                onClickEvent(AnniversaryBottomSheetContact.Event.ShowDeleteDialog)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.DeleteForever,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }

                            IconButton(
                                onClick = { onChangeBottomSheetType(AnniversaryBottomSheetType.EDIT) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = SentyBlack,
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = SentyWhite),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "날짜",
                            style = SentyTheme.typography.labelSmall,
                        )

                        Text(
                            text = stringResource(id = R.string.common_required_star),
                            style = SentyTheme.typography.labelSmall
                                .copy(color = MaterialTheme.colorScheme.error),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${uiState.selectedDate.first}년 ${uiState.selectedDate.second}월 ${uiState.selectedDate.third}일",
                            style = SentyTheme.typography.titleLarge,
                            fontSize = 24.sp
                        )
                        if (type == AnniversaryBottomSheetType.EDIT) {
                            IconButton(onClick = { onClickEvent(AnniversaryBottomSheetContact.Event.ShowCalendar) }) {
                                Icon(
                                    imageVector = Icons.Rounded.Edit,
                                    contentDescription = null,
                                    tint = Color(0xFF848484)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "제목",
                            style = SentyTheme.typography.labelSmall,
                        )

                        Text(
                            text = stringResource(id = R.string.common_required_star),
                            style = SentyTheme.typography.labelSmall
                                .copy(color = MaterialTheme.colorScheme.error),
                        )
                    }

                    if (type == AnniversaryBottomSheetType.EDIT) {
                        SentyTextField(
                            text = uiState.schedule.title,
                            hint = "제목 입력",
                            isError = uiState.isErrorTitle,
                            errorMsg = stringResource(id = R.string.anniversary_bottom_sheet_title_error_message_text),
                            modifier = Modifier.fillMaxWidth(),
                            onChangeText = { onClickEvent(AnniversaryBottomSheetContact.Event.UpdateTitle(it)) },
                            textStyle = SentyTheme.typography.bodyMedium,
                        )
                    } else {
                        SentyReadOnlyTextField(
                            text = uiState.schedule.title,
                            textStyle = SentyTheme.typography.bodyMedium
                                .copy(color = SentyBlack),
                        )
                    }

                    Text(
                        text = "장소",
                        style = SentyTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )

                    if (type == AnniversaryBottomSheetType.EDIT) {
                        SentyTextField(
                            modifier = Modifier.fillMaxWidth(),
                            text = uiState.schedule.location,
                            hint = "장소 입력",
                            errorMsg = "",
                            textStyle = SentyTheme.typography.bodyMedium
                                .copy(color = SentyBlack),
                            onChangeText = { onClickEvent(AnniversaryBottomSheetContact.Event.UpdateLocation(it)) },
                        )
                    } else {
                        SentyReadOnlyTextField(
                            text = uiState.schedule.location,
                            textStyle = SentyTheme.typography.bodyMedium
                                .copy(color = SentyBlack),
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "시간",
                            style = SentyTheme.typography.labelSmall,
                        )
                        Text(
                            text = if (type == AnniversaryBottomSheetType.EDIT && uiState.schedule.time.isEmpty()) "시간 입력"
                            else uiState.schedule.time,
                            style = SentyTheme.typography.bodyMedium
                                .copy(color = if (uiState.schedule.time.isEmpty()) SentyGray50 else SentyBlack),
                            modifier = Modifier.clickable { if (type == AnniversaryBottomSheetType.EDIT) onClickEvent(AnniversaryBottomSheetContact.Event.ShowTimePicker) }
                        )
                    }
                    Text(
                        text = "메모",
                        style = SentyTheme.typography.labelSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    SentyMultipleTextField(
                        text = uiState.schedule.memo,
                        onChangeText = { onClickEvent(AnniversaryBottomSheetContact.Event.UpdateMemo(it)) },
                        readOnly = type == AnniversaryBottomSheetType.READ,
                        textStyle = SentyTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 32.dp),
                    )
                }

                if (type == AnniversaryBottomSheetType.EDIT) {
                    SentyFilledButtonWithProgress(
                        text = stringResource(id = R.string.common_save),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .padding(horizontal = 16.dp),
                        enabled = !uiState.isLoading,
                        onClick = {
                            if (!uiState.isLoading) {
                                scheduleId?.let {
                                    onClickEvent(AnniversaryBottomSheetContact.Event.OnClickUpdate)
                                } ?: onClickEvent(AnniversaryBottomSheetContact.Event.OnClickSave)
                            }
                        },
                    )
                }
            }
        }
    }
}

enum class AnniversaryBottomSheetType {
    READ, EDIT,
}