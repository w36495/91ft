package com.w36495.senty.view.screen.friendgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.util.darken
import com.w36495.senty.util.dropShadow
import com.w36495.senty.view.screen.friendgroup.model.EditFriendGroupContact
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.dialogs.BasicColorPickerDialog
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun EditFriendGroupDialog(
    vm: EditFriendGroupViewModel = hiltViewModel(),
    group: FriendGroupUiModel? = null,
    onShowSnackBar: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is EditFriendGroupContact.Effect.ShowError -> {
                    
                }
                is EditFriendGroupContact.Effect.NavigateFriendGroups -> {
                    onShowSnackBar(effect.message)
                    onDismiss()
                }
            }
        }
    }

    FriendGroupAddContents(
        uiState = uiState,
        group = group,
        onDismiss = { onDismiss() },
        onClickSave = { vm.handleEvent(EditFriendGroupContact.Event.onSaveFriendGroup(it)) },
        onClickEdit = { vm.handleEvent(EditFriendGroupContact.Event.onEditFriendGroup(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupAddContents(
    uiState: EditFriendGroupContact.State,
    group: FriendGroupUiModel?,
    onClickSave: (FriendGroupUiModel) -> Unit,
    onClickEdit: (FriendGroupUiModel) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var inputGroup by remember { mutableStateOf(group?.name ?: "") }
    var showColorPicker by remember { mutableStateOf(false) }
    var selectedColor by remember { mutableStateOf(group?.color ?: FriendGroupUiModel.DEFAULT_COLOR) }
    var isError by remember { mutableStateOf(false) }

    var dialogHeight by remember { mutableStateOf(0) }
    val dialogHeightDp = with(LocalDensity.current) {
        dialogHeight.toDp()
    }

    if (showColorPicker) {
        BasicColorPickerDialog(
            onDismiss = { showColorPicker = false }
        ) { color ->
            selectedColor = color
            showColorPicker = false
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() },
                )
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .onSizeChanged { dialogHeight = it.height }
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = group?.let { R.string.friend_group_title_edit } ?: R.string.friend_group_title_save),
                                style = SentyTheme.typography.headlineSmall
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                        actions = {
                            IconButton(onClick = { onDismiss() }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(selectedColor, RoundedCornerShape(10.dp))
                                .border(2.dp, selectedColor.darken(), RoundedCornerShape(10.dp))
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showColorPicker = true }
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        SentyTextField(
                            text = inputGroup,
                            onChangeText = {
                                inputGroup = when {
                                    it.length <= 8 -> {
                                        isError = false
                                        it
                                    }
                                    else -> inputGroup.substring(0, 8)
                                }
                            },
                            hint = stringResource(id = R.string.friend_group_name_hint_text),
                            hintSize = 14.sp,
                            errorMsg = stringResource(id = R.string.friend_group_error),
                            isError = isError,
                            modifier = Modifier.padding(start = 2.dp),
                            textStyle = SentyTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SentyFilledButton(text = stringResource(id = group?.let { R.string.common_edit} ?: R.string.common_save),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        onClick = {
                            focusManager.clearFocus()

                            if (inputGroup.isEmpty()) {
                                isError = true
                                return@SentyFilledButton
                            }

                            group?.let {
                                val editFriendGroup = it.copy(name = inputGroup, color = selectedColor)
                                onClickEdit(editFriendGroup)
                            } ?: run {
                                val newFriendGroup = FriendGroupUiModel(name = inputGroup, color = selectedColor)
                                onClickSave(newFriendGroup)
                            }
                        }
                    )
                }

                if (uiState is EditFriendGroupContact.State.Loading) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(dialogHeightDp)
                        .background(SentyBlack.copy(0.2f), RoundedCornerShape(12.dp))) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .dropShadow(
                                    shape = RoundedCornerShape(16.dp),
                                    offsetX = 0.dp,
                                    offsetY = 0.dp,
                                    blur = 8.dp
                                )
                                .background(SentyWhite, RoundedCornerShape(16.dp))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendGroupAddDialogScreenPreview() {
    SentyTheme {
        FriendGroupAddContents(
            uiState = EditFriendGroupContact.State.Loading,
            group = null,
            onClickEdit = {},
            onClickSave = {},
            onDismiss = {},
        )
    }
}