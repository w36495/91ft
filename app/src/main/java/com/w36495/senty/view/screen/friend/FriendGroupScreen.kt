package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.viewModel.FriendGroupViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun FriendGroupScreen(
    vm: FriendGroupViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val friendGroups = vm.friendGroups.collectAsStateWithLifecycle()

    val rememberFriendGroups by rememberSaveable(friendGroups) { mutableStateOf(friendGroups) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var selectEditGroup by remember { mutableStateOf(FriendGroup.emptyFriendGroup) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        vm.errorFlow.collectLatest {
            snackbarHostState.showSnackbar(it)
        }
    }

    if (showAddDialog) {
        FriendGroupAddDialog(
            group = if (selectEditGroup != FriendGroup.emptyFriendGroup) selectEditGroup else null,
            onClickSave = {
                if (vm.validateFriendGroup(it)) {
                    vm.saveFriendGroup(it)
                    showAddDialog = false
                }
            },
            onClickEdit = {
                if (vm.validateFriendGroup(it)) {
                    vm.updateFriendGroup(it)
                    showAddDialog = false
                }
            },
            onDismiss = {
                selectEditGroup = FriendGroup.emptyFriendGroup
                showAddDialog = false
            }
        )
    }

    FriendGroupContents(
        friendGroups = rememberFriendGroups.value,
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
        onShowAddDialog = { showAddDialog = true },
        onClickEdit = {
            selectEditGroup = it
            showAddDialog = true
        },
        onClickDelete = { vm.removeFriendGroup(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendGroupContents(
    friendGroups: List<FriendGroup>,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    onShowAddDialog: () -> Unit,
    onClickEdit: (FriendGroup) -> Unit,
    onClickDelete: (String) -> Unit,
) {
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
                    IconButton(onClick = onShowAddDialog) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White,
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            friendGroups.forEachIndexed { index, group ->
                FriendGroupItem(
                    modifier = Modifier.fillMaxWidth(),
                    group = group,
                    onClickDelete = onClickDelete,
                    onClickEdit = onClickEdit,
                )

                if (index != friendGroups.lastIndex) HorizontalDivider()
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
    val coroutineScope = rememberCoroutineScope()

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
            .swipeable(
                state = swipeState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                velocityThreshold = 1000.dp
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = {
                    onClickEdit(group)
                    coroutineScope.launch {
                        swipeState.snapTo(0)
                    }
                },
                color = Color(0xFFF5A61D)
            ) {
                IconButton(
                    onClick = {
                        onClickEdit(group)
                        coroutineScope.launch {
                            swipeState.snapTo(0)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFF5A61D),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.padding(4.dp)
                ) {
                    androidx.compose.material.Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Surface(
                onClick = {
                    showDeleteDialog = true
                    coroutineScope.launch {
                        swipeState.snapTo(0)
                    }
                },
                color = Color.Red,
            ) {
                IconButton(
                    onClick = {
                        showDeleteDialog = true
                        coroutineScope.launch {
                            swipeState.snapTo(0)
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Red,
                    ),
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        ListItem(
            modifier = Modifier.offset { IntOffset(swipeState.offset.value.toInt(), 0) },
            headlineContent = { Text(text = group.name) },
            colors = ListItemDefaults.colors(
                containerColor = Color.White,
            ),
            trailingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(group.getIntTypeColor(), RoundedCornerShape(8.dp))
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
            friendGroups = listOf(
                FriendGroup(name = "친구"),
                FriendGroup(name = "가족"),
                FriendGroup(name = "테스트"),
                FriendGroup(name = "기타"),
            ),
            snackbarHostState = SnackbarHostState(),
            onClickDelete = {},
            onBackPressed = {},
            onShowAddDialog = {},
            onClickEdit = {},
        )
    }
}