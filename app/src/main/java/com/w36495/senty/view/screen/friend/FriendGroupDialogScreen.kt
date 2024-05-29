package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.viewModel.FriendGroupViewModel
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton

@Composable
fun FriendGroupDialogScreen(
    vm: FriendGroupViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onGroupSelected: (FriendGroup) -> Unit,
    onEditClick: () -> Unit,
) {
    val friendGroups by vm.friendGroups.collectAsState()

    FriendGroupContents(
        onDismiss = { onDismiss() },
        friendGroups = friendGroups,
        onGroupSelected = { group ->
            onGroupSelected(group)
            onDismiss()
        },
        onEditClick = { onEditClick() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupContents(
    onDismiss: () -> Unit,
    friendGroups: List<FriendGroup>,
    onGroupSelected: (FriendGroup) -> Unit,
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
                    title = { Text(text = "친구그룹") },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        friendGroups.forEachIndexed { index, group ->
                            FriendGroupItem(
                                group = group,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    onGroupSelected(it)
                                }
                            )

                            if (index < friendGroups.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                    SentyFilledButton(
                        text = "그룹 편집",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        onClick = {
                            onEditClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendGroupItem(
    modifier: Modifier = Modifier,
    group: FriendGroup,
    onClick: (FriendGroup) -> Unit
) {
    Row(
        modifier = modifier.clickable { onClick(group) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = group.name)

        IconButton(
            onClick = { },
            modifier = Modifier.background(
                color = Color(group.getIntTypeColor()),
                RoundedCornerShape(10.dp)
            )
        ) { }
    }
}