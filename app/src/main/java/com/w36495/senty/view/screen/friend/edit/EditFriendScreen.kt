package com.w36495.senty.view.screen.friend.edit

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.screen.friend.edit.contact.EditFriendContact
import com.w36495.senty.view.screen.friendgroup.FriendGroupSelectionDialog
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.dialogs.BasicCalendarDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.SentyGray40
import com.w36495.senty.view.ui.theme.SentyGray50
import com.w36495.senty.view.ui.theme.SentyGray70
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun EditFriendRoute(
    vm: EditFriendViewModel = hiltViewModel(),
    padding: PaddingValues,
    friendId: String?,
    moveToFriendGroups: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(friendId) {
        friendId?.let { vm.getFriend(it) }
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is EditFriendContact.Effect.ShowSnackBar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    vm.sendEffect(EditFriendContact.Effect.NavigateToFriends)
                }
                is EditFriendContact.Effect.ShowError -> {

                }
                EditFriendContact.Effect.NavigateToFriends -> {
                    onBackPressed()
                }
                EditFriendContact.Effect.NavigateToFriendGroups -> {
                    moveToFriendGroups()
                }
            }
        }
    }

    EditFriendScreen(
        uiState = uiState,
        isEditMode = friendId?.let { true } ?: false,
        onChangeName = { vm.handleEvent(EditFriendContact.Event.UpdateFriendName(it)) },
        onChangeFriendGroup = { vm.handleEvent(EditFriendContact.Event.UpdateFriendGroup(it)) },
        onChangeBirthday = { birthday, isSkipped ->
            vm.handleEvent(EditFriendContact.Event.UpdateFriendBirthday(birthday, isSkipped))
        },
        onChangeMemo = { vm.handleEvent(EditFriendContact.Event.UpdateFriendMemo(it)) },
        onClickSave = {
            friendId?.let {
                vm.handleEvent(EditFriendContact.Event.OnClickEdit)
            } ?: vm.handleEvent(EditFriendContact.Event.OnClickSave)
        },
        onClickFriendGroupEdit = { vm.handleEvent(EditFriendContact.Event.OnClickFriendGroups) },
        onClickCalendar = { vm.handleEvent(EditFriendContact.Event.OnClickCalendar) },
        onClickFriendGroupSelectionDialog = { vm.handleEvent(EditFriendContact.Event.OnClickFriendGroupSelectionDialog) },
        onBackPressed = { vm.handleEvent(EditFriendContact.Event.OnClickBack) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditFriendScreen(
    uiState: EditFriendContact.State,
    isEditMode: Boolean = false,
    onChangeName: (String) -> Unit,
    onChangeFriendGroup: (FriendGroupUiModel) -> Unit,
    onChangeBirthday: (String, Boolean) -> Unit,
    onChangeMemo: (String) -> Unit,
    onClickSave: () -> Unit,
    onClickFriendGroupEdit: () -> Unit,
    onClickCalendar: () -> Unit,
    onClickFriendGroupSelectionDialog: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    when {
        uiState.showCalendarDialog -> {
            BasicCalendarDialog(
                onDismiss = { onClickCalendar() },
                onSelectedDate = { year, month, day ->
                    val birthday = "$year-${StringUtils.format2Digits(month + 1)}-${StringUtils.format2Digits(day)}"
                    onChangeBirthday(birthday, uiState.checkBirthdaySkipped)
                }
            )
        }
        uiState.showFriendGroupSelectionDialog -> {
            FriendGroupSelectionDialog(
                onDismiss = { onClickFriendGroupSelectionDialog() },
                onSelectFriendGroup = { onChangeFriendGroup(it) },
                onClickFriendGroupEdit = { onClickFriendGroupEdit() }
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = if (isEditMode) R.string.friend_title_edit else R.string.friend_title_add),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "뒤로가기 버튼 아이콘",
                        )
                    }
                }
            )
        },
        containerColor = SentyWhite,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() },
                ),
        ) {
            val friend = uiState.friend

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 88.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                NameInputSection(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(id = R.string.friend_edit_name_text),
                    text = friend.name,
                    placeHolder = stringResource(id = R.string.friend_edit_name_hint_text),
                    isError = uiState.isErrorName,
                    onChangeText = onChangeName,
                )

                Spacer(modifier = Modifier.height(16.dp))

                GroupSection(
                    modifier = Modifier.fillMaxWidth(),
                    group = FriendGroupUiModel(id = friend.groupId, name = friend.groupName, color = friend.groupColor),
                    isError = uiState.isErrorGroup,
                    onFriendGroupClick = { onClickFriendGroupSelectionDialog() },
                )

                Spacer(modifier = Modifier.height(16.dp))

                BirthdayInputSection(
                    title = stringResource(id = R.string.friend_edit_birthday_text),
                    placeHolder = friend.birthday.ifEmpty {
                        if (uiState.checkBirthdaySkipped) "" else stringResource(id = R.string.friend_edit_birthday_hint_text)
                    },
                    text = if (friend.birthday.isEmpty()) friend.birthday
                    else {
                        val (year, month, day) = friend.birthday.split("-").map { it.toInt() }
                        "${year}년 ${StringUtils.format2Digits(month)}월 ${StringUtils.format2Digits(day)}일"
                    },
                    onChangeText = onChangeBirthday,
                    enable = false,
                    onClick = { onClickCalendar() },
                    isCheckedBirthday = uiState.checkBirthdaySkipped,
                )

                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.friend_edit_memo_text),
                        style = SentyTheme.typography.labelSmall
                            .copy(color = SentyGray70),
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                    )

                    SentyMultipleTextField(
                        text = friend.memo,
                        onChangeText = onChangeMemo,
                        textStyle = SentyTheme.typography.bodyMedium,
                    )
                }
            }

            SentyFilledButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
                text = stringResource(id = if (isEditMode) R.string.common_edit else R.string.common_save),
                onClick = {
                    focusManager.clearFocus()
                    onClickSave()
                },
            )

            if (uiState.isLoading) {
                LoadingCircleIndicator(
                    hasBackGround = false,
                )
            }
        }
    }
}

@Composable
private fun NameInputSection(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    placeHolder: String,
    isError: Boolean,
    onChangeText: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = SentyTheme.typography.labelSmall
                    .copy(color = SentyGray70),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = stringResource(id = R.string.common_required_star),
                style = SentyTheme.typography.labelSmall
                    .copy(color = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        SentyTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            hint = placeHolder,
            errorMsg = stringResource(id = R.string.friend_edit_name_error_text),
            isError = isError,
            onChangeText = {
                onChangeText(it)
            },
            textStyle = SentyTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun BirthdayInputSection(
    modifier: Modifier = Modifier,
    isCheckedBirthday: Boolean,
    title: String,
    text: String,
    placeHolder: String,
    enable: Boolean = false,
    onChangeText: (String, Boolean) -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { if (!isCheckedBirthday) onClick() }
            .padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = SentyTheme.typography.labelSmall
                    .copy(color = SentyGray70),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isCheckedBirthday,
                    onCheckedChange = {
                        if (it) onChangeText("", !isCheckedBirthday)
                        else onChangeText(text, !isCheckedBirthday)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = SentyGray70,
                        uncheckedColor = SentyGray40,
                    ),
                )

                Text(
                    text = stringResource(id = R.string.friend_edit_birthday_checkable_text),
                    style = SentyTheme.typography.labelSmall,
                )
            }
        }

        SentyTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            hint = placeHolder,
            errorMsg = "",
            onChangeText = { onChangeText(it, isCheckedBirthday) },
            enabled = enable,
            textStyle = SentyTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun GroupSection(
    modifier: Modifier = Modifier,
    group: FriendGroupUiModel,
    isError: Boolean,
    onFriendGroupClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onFriendGroupClick() }
            .padding(horizontal = 16.dp)
    ) {
        Row (
            modifier = Modifier.padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.friend_edit_group_text),
                style = SentyTheme.typography.labelSmall
                    .copy(color = SentyGray70),
            )

            Text(
                text = stringResource(id = R.string.common_required_star),
                style = SentyTheme.typography.labelSmall
                    .copy(color = MaterialTheme.colorScheme.error),
            )
        }

        SentyReadOnlyTextField(
            text = stringResource(id = if (isError) R.string.friend_edit_group_ereror_text else R.string.friend_edit_group_hint_text),
            group = group,
            showChip = group != FriendGroupUiModel.emptyFriendGroup,
            textColor = if (isError) MaterialTheme.colorScheme.error else SentyGray50,
            dividerColor = if (isError) MaterialTheme.colorScheme.error else SentyGreen60,
            textStyle = SentyTheme.typography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun CalendarPreview() {
    SentyTheme {
        EditFriendScreen(
            uiState = EditFriendContact.State(isLoading = false),
            isEditMode = false,
            onChangeName = {},
            onChangeFriendGroup = {},
            onChangeBirthday = { _, _ -> },
            onChangeMemo = {},
            onClickSave = {},
            onClickFriendGroupEdit = {},
            onBackPressed = {},
            onClickCalendar = {},
            onClickFriendGroupSelectionDialog = {},
        )
//        var isCheckedBirthday by remember { mutableStateOf(false) }
//        var birthday by remember { mutableStateOf("") }
//
//
//        BirthdayInputSection(
//            title = "생일",
//            placeHolder = if (birthday.isEmpty()) "생일을 입력하세요." else birthday,
//            text = if (birthday.isEmpty()) birthday
//            else {
//                val (year, month, day) = birthday.split("-").map { it.toInt() }
//                "${year}년 ${StringUtils.format2Digits(month)}월 ${StringUtils.format2Digits(day)}일"
//            },
//            onChangeText = {
//                if (isCheckedBirthday) birthday = ""
//                else birthday = it
//            },
//            enable = isCheckedBirthday,
//            onClick = {  true },
//            isCheckedBirthday = isCheckedBirthday,
//            onChangeCheckState = { isCheckedBirthday = !isCheckedBirthday }
//        )
    }
}