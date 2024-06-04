package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.FriendEditUiState
import com.w36495.senty.viewModel.FriendEditViewModel

@Composable
fun FriendEditRoute(
    vm: FriendEditViewModel = hiltViewModel(),
    friendId: String,
    onBackPressed: () -> Unit,
    onMoveFriendList: () -> Unit,
    onClickGroupEdit: () -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.getFriend(friendId)
    }

    val uiState by vm.friend.collectAsStateWithLifecycle()

    FriendEditScreen(
        uiState = uiState,
        onBackPressed = onBackPressed,
        onClickSave = { friend ->
            vm.updateFriend(friend)
            onMoveFriendList()
        },
        onClickGroupEdit = onClickGroupEdit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendEditScreen(
    uiState: FriendEditUiState,
    onBackPressed: () -> Unit,
    onClickSave: (FriendDetail) -> Unit,
    onClickGroupEdit: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "친구수정") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                FriendEditUiState.Loading -> {
                    FriendEditLoading(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is FriendEditUiState.Success -> {
                    FriendEditContents(
                        modifier = Modifier.fillMaxSize(),
                        friendDetail = uiState.friend,
                        onClickSave = onClickSave,
                        onClickGroupEdit = onClickGroupEdit
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendEditLoading(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            onClick = { }, enabled = false,
            colors = CardDefaults.cardColors(
                disabledContentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(color = Green40)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "데이터를 불러오고 있습니다.",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun FriendEditContents(
    modifier: Modifier = Modifier,
    friendDetail: FriendDetail,
    onClickSave: (FriendDetail) -> Unit,
    onClickGroupEdit: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(friendDetail.name) }
    var memo by rememberSaveable { mutableStateOf(friendDetail.memo) }
    var birthday by rememberSaveable { mutableStateOf(friendDetail.birthday) }
    var group by remember { mutableStateOf(friendDetail.friendGroup) }

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        FriendGroupDialogScreen(
            onDismiss = { showDialog = false },
            onGroupSelected = {
                group = it
                showDialog = false
            },
            onEditClick = { onClickGroupEdit() }
        )
    }
    Column(
        modifier = modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            InputSection(
                modifier = Modifier.fillMaxWidth(),
                title = "이름",
                text = name,
                placeHolder = if (name.isNotEmpty()) "이름을 입력하세요." else name,
                onChangeText = {
                    name = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            GroupSection(
                modifier = Modifier.fillMaxWidth(),
                group = group,
                onFriendGroupClick = {
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputSection(
                title = "생일",
                placeHolder = if (birthday.isNotEmpty()) "생일을 입력하세요. (ex. 1117)" else birthday,
                text = birthday,
                onChangeText = { birthday = it },
            )

            Text(
                text = "메모", style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp, top = 16.dp)
            )
            SentyMultipleTextField(
                text = memo,
                onChangeText = { memo = it }
            )
        }

        SentyFilledButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            text = "수정",
            onClick = {
                val friend = FriendDetail(
                    name = name,
                    memo = memo,
                    birthday = birthday,
                    friendGroup = group
                ).apply { setId(friendDetail.id) }

                onClickSave(friend)
            }
        )
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
            onChangeText = onChangeText
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
            text = group.name,
            group = group,
            showChip = group != FriendGroup.emptyFriendGroup,
            textColor = MaterialTheme.colorScheme.onSurface,
            dividerColor = Green40
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendEditScreenPreview() {
    SentyTheme {
        FriendEditScreen(
            uiState = FriendEditUiState.Loading,
            onBackPressed = { /*TODO*/ },
            onClickSave = {},
            onClickGroupEdit = {}
        )
    }
}