package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.FriendEntity
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.FriendAddViewModel

@Composable
fun FriendAddScreen(
    vm: FriendAddViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onMoveFriendList: () -> Unit,
) {
    var group by remember { mutableStateOf(FriendGroup.emptyFriendGroup) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        FriendGroupDialogScreen(
            onDismiss = { showDialog = false },
            onGroupSelected = {
                group = it
                showDialog = false
            },
            onEditClick = {
                // TODO : 그룹 편집 화면으로 이동
            }
        )
    }

    FriendAddContents(
        group = group,
        onClickFriendGroup = { showDialog = true },
        onBackPressed = { onBackPressed() },
        onClickSave = { friend ->
            vm.saveFriend(friend)
            onMoveFriendList()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendAddContents(
    onBackPressed: () -> Unit,
    onClickSave: (FriendEntity) -> Unit,
    onClickFriendGroup: () -> Unit,
    group: FriendGroup
) {
    var name by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "친구등록") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                InputSection(
                    modifier = Modifier.fillMaxWidth(),
                    title = "이름",
                    text = name,
                    placeHolder = "이름을 입력하세요.",
                    onChangeText = {
                        name = it
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                GroupSection(
                    modifier = Modifier.fillMaxWidth(),
                    group = group,
                    onFriendGroupClick = {
                        onClickFriendGroup()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputSection(
                    title = "생일",
                    placeHolder = "생일을 입력하세요. (ex. 1117)",
                    text = birthday,
                    onChangeText = { birthday = it },
                )

                Text(
                    text = "메모", style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp, top = 16.dp)
                )
                SentyMultipleTextField(
                    text = memo,
                    onChangeText = {
                        memo = it
                    }
                )
            }

            SentyFilledButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                text = "등록",
                onClick = {
                    val friend = FriendEntity(
                        name = name,
                        birthday = birthday,
                        memo = memo
                    ).apply { setFriendGroup(group) }

                    onClickSave(friend)
                }
            )
        }
    }
}

@Composable
private fun InputSection(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    placeHolder: String,
    onChangeText: (String) -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        SentyTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            hint = placeHolder,
            errorMsg = "",
            onChangeText = {
                onChangeText(it)
            }
        )
    }
}

@Composable
private fun GroupSection(
    modifier: Modifier = Modifier,
    group: FriendGroup,
    onFriendGroupClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onFriendGroupClick() }
    ) {
        Text(
            text = "그룹",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        SentyReadOnlyTextField(
            text = "그룹을 선택하세요.",
            group = group,
            showChip = group != FriendGroup.emptyFriendGroup,
            textColor = MaterialTheme.colorScheme.onSurface,
            dividerColor = Green40
        )
    }
}