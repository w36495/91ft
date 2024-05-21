package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyElevatedButton
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.viewModel.FriendDetailViewModel

@Composable
fun FriendDetailScreen(
    friendId: String,
    vm: FriendDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onClickEdit: (FriendDetail) -> Unit,
    onClickDelete: () -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.getFriend(friendId)
    }
    val friend by vm.friend.collectAsState()

    FriendDetailContents(
        friend = friend,
        onBackPressed = { onBackPressed() },
        onClickEdit = { onClickEdit(it) },
        onClickDelete = { onClickDelete() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendDetailContents(
    modifier: Modifier = Modifier,
    friend: FriendDetail,
    onBackPressed: () -> Unit,
    onClickEdit: (FriendDetail) -> Unit,
    onClickDelete: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "친구정보") },
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
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                FriendInfoSection(friend = friend)
            }

            BottomButtons(
                onClickEdit = { onClickEdit(friend) },
                onClickDelete = { onClickDelete() }
            )
        }
    }
}

@Composable
private fun FriendInfoSection(
    modifier: Modifier = Modifier,
    friend: FriendDetail
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Column(
            modifier = modifier
        ) {
            Text(
                text = "이름",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            SentyReadOnlyTextField(
                text = friend.name,
                textColor = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "그룹",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .padding(top = 32.dp)
            )

            friend.group?.let {
                SentyReadOnlyTextField(
                    text = it.name,
                    group = it,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }


            Text(
                text = "생일",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp, top = 32.dp)
            )

            SentyReadOnlyTextField(
                text = if (friend.birthday.isEmpty()) "" else friend.displayBirthday(),
                textColor = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "메모",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp, top = 32.dp)
            )
            SentyMultipleTextField(
                text = friend.memo,
                onChangeText = { },
                readOnly = true
            )
        }
    }
}

@Composable
private fun GiftSection() {

}

@Composable
private fun BottomButtons(
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Column(modifier = modifier) {
        SentyFilledButton(
            text = "수정",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) { onClickEdit() }

        Spacer(modifier = Modifier.height(6.dp))

        SentyElevatedButton(
            text = "삭제",
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) { onClickDelete() }
    }
}

@Preview(showBackground = true)
@Composable
fun FriendDetailPreview() {
    SentyTheme {
        FriendDetailContents(friend = FriendDetail(
            name = "김철수",
            birthday = "1117",
            memo = "",
        ).apply {
            setFriendGroup(
                FriendGroup(
                    id = "1",
                    name = "친구"
                )
            )
        },
            onBackPressed = {},
            onClickEdit = {},
            onClickDelete = {}
        )
        GiftSection()
    }
}