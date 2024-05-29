package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material3.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.viewModel.FriendGroupViewModel

@Composable
fun FriendGroupScreen(
    vm: FriendGroupViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val groups by vm.friendGroups.collectAsState()

    FriendGroupContents(
        groups = groups,
        onClickEdit = {},
        onClickDelete = { vm.removeFriendGroup(it) },
        onBackPressed = { onBackPressed() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupContents(
    groups: List<FriendGroup>,
    onBackPressed: () -> Unit,
    onClickEdit: (FriendGroup) -> Unit,
    onClickDelete: (String) -> Unit,
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    if (showAddDialog) {
        FriendGroupAddDialogScreen(
            onDismiss = { showAddDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "친구그룹") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }, actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                )
            )
        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            groups.forEachIndexed { index, group ->
                FriendGroupItem(
                    group = group,
                    onClickEdit = {},
                    onClickDelete = { onClickDelete(it) },
                )

                if (index != groups.lastIndex) HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FriendGroupItem(
    modifier: Modifier = Modifier,
    group: FriendGroup,
    onClickEdit: (FriendGroup) -> Unit,
    onClickDelete: (String) -> Unit,
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val swipeState = rememberSwipeableState(initialValue = 0)

    val squareSize = 112.dp
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)

    if (showDeleteDialog) {
        BasicAlertDialog(
            title = "그룹을 삭제하시겠습니까?",
            discContent = {
                Text(
                    text = "해당 그룹으로 설정되어있는 친구들도 모두 함께 삭제됩니다. 삭제된 그룹은 복구가 불가능합니다.",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
            },
            onComplete = {
                onClickDelete(group.id)
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(IntrinsicSize.Min)
            .swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                velocityThreshold = 1000.dp
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color(0xFFF5A61D))
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = { onClickEdit(group) },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFF5A61D),
                        contentColor = Color.White
                    )
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color.Red)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Red,
                    )
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        ListItem(
            modifier = Modifier
                .fillMaxHeight()
                .offset {
                    IntOffset(swipeState.offset.value.toInt(), 0)
                },
            headlineContent = { Text(text = group.name) },
            colors = ListItemDefaults.colors(
                containerColor = Color.White,
            ),
            trailingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(group.getIntTypeColor()), RoundedCornerShape(8.dp))
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FriendGroupPreview() {
    SentyTheme {
        FriendGroupContents(
            groups = listOf(
                FriendGroup(name = "친구"),
                FriendGroup(name = "가족"),
                FriendGroup(name = "기타"),
            ),
            onClickEdit = {},
            onClickDelete = {},
            onBackPressed = {}
        )
    }
}