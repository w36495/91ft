package com.w36495.senty.view.screen.friend.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.screen.friend.list.contact.FriendContact
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.chips.FriendGroupChip
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray70
import com.w36495.senty.view.ui.theme.SentyGray80
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyPink60
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.SentyYellow60
import kotlinx.coroutines.launch

@Composable
fun FriendRoute(
    vm: FriendViewModel = hiltViewModel(),
    padding: PaddingValues,
    moveToFriendAdd: () -> Unit,
    moveToFriendGroup: () -> Unit,
    moveToFriendDetail: (String) -> Unit,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        vm.effect.collect {effect ->
            when (effect) {
                FriendContact.Effect.NavigateToFriendAdd -> { moveToFriendAdd() }
                FriendContact.Effect.NavigateToFriendGroups -> { moveToFriendGroup() }
                is FriendContact.Effect.NavigateToFriendDetail -> {
                    moveToFriendDetail(effect.friendId)
                }
                is FriendContact.Effect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    FriendScreen(
        modifier = Modifier.padding(bottom = 48.dp),
        uiState = uiState,
        snackBarHostState = snackBarHostState,
        onClickGroupSetting = { vm.handleEvent(FriendContact.Event.OnClickFriendGroups) },
        onClickFriend = { vm.handleEvent(FriendContact.Event.OnClickFriendDetail(it)) },
        onClickFriendAdd = { vm.handleEvent(FriendContact.Event.OnClickFriendAdd) },
        onChangeFriendsByFriendGroup = { vm.handleEvent(FriendContact.Event.OnSelectFriendGroup(it)) }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendScreen(
    modifier: Modifier = Modifier,
    uiState: FriendContact.State,
    snackBarHostState: SnackbarHostState,
    onClickGroupSetting: () -> Unit,
    onClickFriend: (String) -> Unit,
    onClickFriendAdd: () -> Unit,
    onChangeFriendsByFriendGroup: (FriendGroupUiModel?) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var showProgressBar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.friend_title),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onClickGroupSetting() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "친구 그룹 목록 아이콘",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onClickFriendAdd() },
                shape = CircleShape,
                containerColor = SentyYellow60,
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = SentyWhite,
        modifier = modifier,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() },
                ),
        ) {
            when (uiState) {
                FriendContact.State.Idle -> {
                    showProgressBar = false
                }
                FriendContact.State.Loading -> {
                    showProgressBar = true
                }
                is FriendContact.State.Success -> {
                    showProgressBar = false

                    val friends = uiState.friends
                    val friendGroups = uiState.friendGroups

                    FriendContents(
                        friends = friends,
                        friendGroups = friendGroups,
                        onClickFriend = onClickFriend,
                        onChangeFriendGroup = { onChangeFriendsByFriendGroup(it) },
                        onChangeFriendGroupAll = { onChangeFriendsByFriendGroup(null) }
                    )
                }
            }

            if (showProgressBar) {
                LoadingCircleIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
private fun FriendContents(
    modifier: Modifier = Modifier,
    friends: List<FriendUiModel>,
    friendGroups: List<FriendGroupUiModel>,
    onClickFriend: (String) -> Unit,
    onChangeFriendGroupAll: () -> Unit,
    onChangeFriendGroup: (FriendGroupUiModel) -> Unit,
) {
    val pagerState = rememberPagerState(
        pageCount = { friendGroups.size },
        initialPage = 0,
    )

    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            0 -> { onChangeFriendGroupAll() }
            else -> { onChangeFriendGroup(friendGroups[pagerState.currentPage]) }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        FriendTopTabLow(
            modifier = Modifier.fillMaxWidth(),
            friendGroups = friendGroups,
            pagerState = pagerState,
        )

        Spacer(modifier = Modifier.height(4.dp))

        HorizontalPager(
            modifier = modifier,
            state = pagerState,
        ) {page ->
            val friendGroup = friendGroups[page]

            if (friends.isEmpty()) {
                FriendViewPagerEmptyItem(
                    friendGroup = friendGroup
                )
            } else {
                FriendViewPagerItemContainer(
                    friends = friends,
                    onClickFriend = onClickFriend,
                )
            }
        }
    }
}

@Composable
private fun FriendTopTabLow(
    modifier: Modifier = Modifier,
    friendGroups: List<FriendGroupUiModel>,
    pagerState: PagerState,
) {
    val currentTabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    ScrollableTabRow(
        modifier = modifier,
        selectedTabIndex = currentTabIndex,
        contentColor = Color.Black,
        containerColor = Color.White,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(it[currentTabIndex]),
                color = SentyGreen60
            )
        },
        edgePadding = 0.dp
    ) {
        friendGroups.forEachIndexed { index, friendGroup ->
            Tab(
                modifier = Modifier.clip(RoundedCornerShape(10.dp)),
                selected = currentTabIndex == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            ) {
                Text(
                    text = friendGroup.name,
                    modifier = Modifier.padding(
                        vertical = 12.dp,
                        horizontal = 16.dp
                    ),
                    style = if (currentTabIndex == index) SentyTheme.typography.titleMedium else SentyTheme.typography.bodyMedium,
                    color = SentyGray80
                )
            }
        }
    }
}

@Composable
fun FriendViewPagerEmptyItem(
    modifier: Modifier = Modifier,
    friendGroup: FriendGroupUiModel,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = if (friendGroup.name == FriendGroupUiModel.allFriendGroup.name) {
                stringResource(id = R.string.friend_empty_group_all_text)
            } else {
                friendGroup.name + stringResource(id = R.string.friend_empty_group_text)
            },
            style = SentyTheme.typography.bodySmall
                .copy(color = SentyGray70),
        )
    }
}
@Composable
fun FriendViewPagerItemContainer(
    modifier: Modifier = Modifier,
    friends: List<FriendUiModel>,
    onClickFriend: (String) -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(friends.size) {index ->
                FriendViewPagerItem(
                    friend = friends[index],
                    onClickFriend = { onClickFriend(friends[index].id) },
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

@Composable
fun FriendViewPagerItem(
    modifier: Modifier = Modifier,
    friend: FriendUiModel,
    onClickFriend: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClickFriend() }
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FriendGroupChip(
                text = friend.groupName,
                chipColor = friend.groupColor,
            )

            Text(
                text = if (friend.birthday.isEmpty()) "" else friend.displayBirthdayOnlyDate(),
                style = SentyTheme.typography.labelMedium
                    .copy(color = SentyGray70),
            )
        }
        Text(
            text = friend.name,
            style = SentyTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = friend.memo,
            style = SentyTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.common_heart),
                style = SentyTheme.typography.bodySmall
                    .copy(color = SentyPink60)
            )

            Text(
                text = stringResource(id = R.string.common_received_gift_text),
                style = SentyTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp),
            )

            Text(
                text = friend.received.toString(),
                style = SentyTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp)
            )

            Text(
                text = stringResource(id = R.string.common_heart),
                style = SentyTheme.typography.bodySmall
                    .copy(color = SentyYellow60),
                modifier = Modifier.padding(start = 12.dp),
            )

            Text(
                text = stringResource(id = R.string.common_sent_gift_text),
                style = SentyTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = friend.sent.toString(),
                style = SentyTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}