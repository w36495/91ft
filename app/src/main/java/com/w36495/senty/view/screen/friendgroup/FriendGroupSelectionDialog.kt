package com.w36495.senty.view.screen.friendgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.friendgroup.list.FriendGroupViewModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray70

@Composable
fun FriendGroupSelectionDialog(
    vm: FriendGroupViewModel = hiltViewModel(),
    onSelectFriendGroup: (FriendGroupUiModel) -> Unit,
    onClickFriendGroupEdit: () -> Unit,
    onDismiss: () -> Unit,
) {
    val friendGroups by vm.friendGroups.collectAsStateWithLifecycle()

    FriendGroupSelectionContent(
        onDismiss = { onDismiss() },
        friendGroups = friendGroups,
        onSelectFriendGroup = onSelectFriendGroup,
        onClickFriendGroupEdit = onClickFriendGroupEdit,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupSelectionContent(
    onDismiss: () -> Unit,
    friendGroups: List<FriendGroupUiModel>,
    onSelectFriendGroup: (FriendGroupUiModel) -> Unit,
    onClickFriendGroupEdit: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .height(360.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.friend_group_title_read),
                            style = SentyTheme.typography.headlineSmall,
                        )
                            },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "친구 그룹 선택 다이얼로그 닫기 아이콘",
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                )

                if (friendGroups.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(Color(0xFFFBFBFB))
                            .padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(id = R.string.friend_group_empty_text),
                            style = SentyTheme.typography.bodyMedium
                                .copy(color = SentyGray70),
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        friendGroups.forEachIndexed { index, group ->
                            FriendGroupItem(
                                group = group,
                                onClick = onSelectFriendGroup,
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
                }


                SentyFilledButton(
                    text = stringResource(id = R.string.friend_group_selection_edit_button_text),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = onClickFriendGroupEdit,
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
        FriendGroupSelectionContent(
            onDismiss = {},
            friendGroups = List(5) {
                FriendGroupUiModel.allFriendGroup
            },
            onSelectFriendGroup = {},
            onClickFriendGroupEdit = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendGroupEmptyContentsPreview() {
    SentyTheme {
        FriendGroupSelectionContent(
            onDismiss = {},
            friendGroups = emptyList(),
            onSelectFriendGroup = {},
            onClickFriendGroupEdit = {},
        )
    }
}