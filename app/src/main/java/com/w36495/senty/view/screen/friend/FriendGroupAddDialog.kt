package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.viewModel.FriendGroupViewModel

@Composable
fun FriendGroupAddDialog(
    vm: FriendGroupViewModel = hiltViewModel(),
    group: FriendGroup? = null,
    onDismiss: () -> Unit
) {
    FriendGroupAddContents(
        group = group,
        onDismiss = { onDismiss() },
        onClickSave = {
            vm.saveFriendGroup(it)
            onDismiss()
        },
        onClickEdit = {
            vm.updateFriendGroup(it)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupAddContents(
    group: FriendGroup? = null,
    onDismiss: () -> Unit,
    onClickSave: (FriendGroup) -> Unit,
    onClickEdit: (FriendGroup) -> Unit,
) {
    var inputGroup by remember { mutableStateOf(group?.name ?: "") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text(text = if (group == null) "그룹등록" else "그룹수정") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFD9D9D9), RoundedCornerShape(8.dp))
                            .clickable { }
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    SentyTextField(
                        text = inputGroup,
                        onChangeText = { inputGroup = it },
                        hint = "그룹명을 입력하세요. (최대 8자)",
                        hintSize = 14.sp,
                        errorMsg = "",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                SentyFilledButton(text = if (group == null) "등록" else "수정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {
                        if (group == null) {
                            onClickSave(FriendGroup(name = inputGroup))
                        } else {
                            onClickEdit(group.copy(name = inputGroup).apply { setId(group.id) })
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendGroupAddDialogScreenPreview() {
    SentyTheme {
        FriendGroupAddContents(
            onDismiss = {},
            onClickSave = {},
            onClickEdit = {}
        )
    }
}