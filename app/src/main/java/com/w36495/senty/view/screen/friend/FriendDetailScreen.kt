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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.util.getTextColorByBackgroundColor
import com.w36495.senty.view.entity.FriendEntity
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyElevatedButton
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.chips.FriendGroupChip
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.viewModel.FriendDetailViewModel

@Composable
fun FriendDetailScreen(
    friendId: String,
    vm: FriendDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val friend by vm.friend.collectAsStateWithLifecycle()
    vm.getFriend(friendId)

    FriendDetailContents(
        friend = friend,
        onBackPressed = { onBackPressed() },
        onClickEdit = { onClickEdit() },
        onClickDelete = { onClickDelete() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendDetailContents(
    modifier: Modifier = Modifier,
    friend: FriendEntity,
    onBackPressed: () -> Unit,
    onClickEdit: () -> Unit,
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
            Column{
                FriendInfoSection(friend = friend)
            }

            BottomButtons(
                onClickEdit = { onClickEdit() },
                onClickDelete = { onClickDelete() }
            )
        }
    }
}

@Composable
private fun FriendInfoSection(
    modifier: Modifier = Modifier,
    friend: FriendEntity
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
                showChip = false,
                chip = {},
                textColor = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "그룹",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .padding(top = 32.dp)
            )

            SentyReadOnlyTextField(
                text = friend.group?.name ?: "",
                showChip = true,
                chip = {
                    FriendGroupChip(
                        modifier = it,
                        text = friend.group?.name,
                        chipColor = friend.group?.getIntTypeColor(),
                        textColor = friend.group?.color?.getTextColorByBackgroundColor()
                    )
                },
                textColor = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "생일",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp, top = 32.dp)
            )

            SentyReadOnlyTextField(
                text = if (friend.birthday.isEmpty()) "" else friend.displayBirthday(),
                showChip = false,
                chip = {},
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
        FriendDetailContents(friend = FriendEntity(
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