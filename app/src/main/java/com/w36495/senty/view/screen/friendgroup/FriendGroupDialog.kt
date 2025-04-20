package com.w36495.senty.view.screen.friendgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.viewModel.FriendGroupViewModel

@Composable
fun FriendGroupDialogScreen(
    vm: FriendGroupViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onGroupSelected: (FriendGroupUiModel) -> Unit,
    onEditClick: () -> Unit,
) {
    val friendGroups by vm.friendGroups.collectAsStateWithLifecycle()

    FriendGroupContents(
        onDismiss = { onDismiss() },
        friendGroups = friendGroups,
        onGroupSelected = { group ->
            onGroupSelected(group)
            onDismiss()
        },
        onEditClick = { onEditClick() },
        onClickRefresh = {  }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupContents(
    onDismiss: () -> Unit,
    friendGroups: List<FriendGroupUiModel>,
    onGroupSelected: (FriendGroupUiModel) -> Unit,
    onClickRefresh: () -> Unit,
    onEditClick: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.friend_group_title_read),
                            style = SentyTheme.typography.headlineSmall,
                        )
                            },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { onClickRefresh() }) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    friendGroups.forEachIndexed { index, group ->
                        FriendGroupItem(
                            group = group,
                            onClick = {
                                onGroupSelected(it)
                            }
                        )

                        if (index < friendGroups.lastIndex) {
                            HorizontalDivider(
                                color = SentyGray20,
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                    }
                }

                SentyFilledButton(
                    text = "그룹 편집",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = {
                        onEditClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun FriendGroupItem(
    modifier: Modifier = Modifier,
    group: FriendGroupUiModel,
    onClick: (FriendGroupUiModel) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(group) }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = group.name,
            style = SentyTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .weight(1f),
        )

        Box(
            modifier = Modifier
                .size(14.dp)
                .background(
                    color = group.color,
                    shape = CircleShape,
                ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendGroupContentsPreview() {
    SentyTheme {
        FriendGroupContents(
            onDismiss = {},
            friendGroups = List(5) {
                FriendGroupUiModel.allFriendGroup
            },
            onGroupSelected = {},
            onClickRefresh = {},
            onEditClick = {}
        )
    }
}