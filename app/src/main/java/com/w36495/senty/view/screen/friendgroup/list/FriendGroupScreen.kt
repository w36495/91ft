package com.w36495.senty.view.screen.friendgroup.list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.util.darken
import com.w36495.senty.view.screen.friendgroup.EditFriendGroupDialog
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray60
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun FriendGroupScreen(
    vm: FriendGroupViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val friendGroups by vm.friendGroups.collectAsStateWithLifecycle()

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var selectEditGroup by remember { mutableStateOf<FriendGroupUiModel?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        vm.snackbarMsg.collectLatest {
            snackBarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(true) {
        vm.errorFlow.collect { onShowGlobalErrorSnackBar(it) }
    }

    if (showAddDialog) {
        EditFriendGroupDialog(
            group = selectEditGroup,
            onShowSnackBar = {
                vm.sendSnackBar(it)
            },
            onDismiss = {
                selectEditGroup = null
                showAddDialog = false
            }
        )
    }

    FriendGroupContents(
        friendGroups = friendGroups,
        snackbarHostState = snackBarHostState,
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
    friendGroups: List<FriendGroupUiModel>,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    onShowAddDialog: () -> Unit,
    onClickEdit: (FriendGroupUiModel) -> Unit,
    onClickDelete: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.friend_group_title),
                        style = SentyTheme.typography.headlineSmall,
                    )
                        },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "뒤로가기 아이콘",
                        )
                    }
                }, actions = {
                    IconButton(onClick = onShowAddDialog) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "친구 그룹 추가 아이콘",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (friendGroups.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFBFBFB)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(id = R.string.friend_group_empty_text),
                        textAlign = TextAlign.Center,
                        style = SentyTheme.typography.labelMedium.copy(color = SentyGray60),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = friendGroups,
                        key = { it.id },
                    ) {friendGroup ->
                        FriendGroupItem(
                            modifier = Modifier.fillMaxWidth(),
                            group = friendGroup,
                            onClickDelete = onClickDelete,
                            onClickEdit = onClickEdit,
                        )

                        HorizontalDivider(
                            color = SentyGray20,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FriendGroupItem(
    modifier: Modifier = Modifier,
    group: FriendGroupUiModel,
    onClickEdit: (FriendGroupUiModel) -> Unit,
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
            title = stringResource(id = R.string.friend_group_delete_title),
            message = stringResource(id = R.string.friend_group_delete_text),
            hasCancel = true,
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
                        contentDescription = "친구 그룹 편집 아이콘",
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
                        contentDescription = "친구 그룹 삭제 아이콘",
                        tint = Color.White
                    )
                }
            }
        }

        ListItem(
            modifier = Modifier.offset { IntOffset(swipeState.offset.value.toInt(), 0) },
            headlineContent = {
                Text(
                    text = group.name,
                    style = SentyTheme.typography.bodyMedium,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.White,
            ),
            trailingContent = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(group.color, RoundedCornerShape(10.dp))
                        .border(
                            width = 1.dp,
                            color = group.color.darken(0.1f),
                            shape = RoundedCornerShape(10.dp),
                        )
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
                FriendGroupUiModel(id = "1", name = "친구", Color(0xFFAAEEEE)),
                FriendGroupUiModel(id = "1", name = "가족", Color(0xFFAAEEEE)),
                FriendGroupUiModel(id = "1", name = "테스트", Color(0xFFAAEEEE)),
                FriendGroupUiModel(id = "1", name = "전체", Color(0xFFAAEEEE)),
            ),
            snackbarHostState = SnackbarHostState(),
            onClickDelete = {},
            onBackPressed = {},
            onShowAddDialog = {},
            onClickEdit = {},
        )
    }
}