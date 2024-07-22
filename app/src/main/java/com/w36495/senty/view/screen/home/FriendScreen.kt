package com.w36495.senty.view.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.w36495.senty.util.getTextColorByBackgroundColor
import com.w36495.senty.view.entity.Friend
import com.w36495.senty.view.entity.FriendGroup
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButtonWithIcon
import com.w36495.senty.view.ui.component.chips.FriendGroupChip
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.FriendViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    vm: FriendViewModel = hiltViewModel(),
    onClickGroupSetting: () -> Unit,
    onClickAddFriend: () -> Unit,
    onClickFriend: (String) -> Unit,
) {
    val friends by vm.friends.collectAsStateWithLifecycle()
    val friendGroups by vm.friendGroups.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "친구목록") },
                actions = {
                    IconButton(onClick = { onClickGroupSetting() }) {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        FriendContents(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            friends = friends,
            friendGroups = friendGroups,
            onClickFriend = { friend -> onClickFriend(friend.friendDetail.id) },
            onClickAddFriend = { onClickAddFriend() },
            onChangeFriendGroup = { vm.getFriendsByFriendGroup(it) },
            onChangeFriendGroupAll = { vm.getFriendsByFriendGroup(null) }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun FriendContents(
    modifier: Modifier = Modifier,
    friends: List<Friend>,
    friendGroups: List<FriendGroup>,
    onClickFriend: (Friend) -> Unit,
    onClickAddFriend: () -> Unit,
    onChangeFriendGroupAll: () -> Unit,
    onChangeFriendGroup: (FriendGroup) -> Unit,
) {
    val viewPagerState = rememberPagerState(
        pageCount = friendGroups.size,
        initialPage = 0,
        infiniteLoop = true,
    )

    LaunchedEffect(viewPagerState.currentPage) {
        when (viewPagerState.currentPage) {
            0 -> { onChangeFriendGroupAll() }
            else -> { onChangeFriendGroup(friendGroups[viewPagerState.currentPage]) }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        SentyOutlinedButtonWithIcon(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = "친구 등록",
            icon = Icons.Default.Add,
            onClick = { onClickAddFriend() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        FriendTopTabLow(
            modifier = Modifier.fillMaxWidth(),
            friendGroups = friendGroups,
            pagerState = viewPagerState
        )

        Spacer(modifier = Modifier.height(4.dp))

        FriendViewPager(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            pagerState = viewPagerState,
            friendList = friends,
            friendGroup = friendGroups[viewPagerState.currentPage],
            onClickFriend = { onClickFriend(it) }
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun FriendTopTabLow(
    modifier: Modifier = Modifier,
    friendGroups: List<FriendGroup>,
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
                color = Green40
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
                    style = MaterialTheme.typography.labelLarge,
                    color = if (index == currentTabIndex) Green40 else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun FriendViewPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    friendList: List<Friend>,
    friendGroup: FriendGroup,
    onClickFriend: (Friend) -> Unit,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) {
        when (pagerState.currentPage) {
            0 -> {
                FriendViewPagerItemContainer(
                    friendList = friendList,
                    friendGroup = friendGroup,
                    isShowFriendGroup = true,
                    onClickFriend = onClickFriend
                )
            }
            else -> {
                FriendViewPagerItemContainer(
                    friendList = friendList,
                    friendGroup = friendGroup,
                    isShowFriendGroup = false,
                    onClickFriend = onClickFriend
                )
            }
        }
    }
}

@Composable
fun FriendViewPagerItemContainer(
    modifier: Modifier = Modifier,
    friendList: List<Friend>,
    friendGroup: FriendGroup,
    isShowFriendGroup: Boolean,
    onClickFriend: (Friend) -> Unit,
) {
    if (friendList.isEmpty()) {
        EmptyFriendInGroup(
            modifier = Modifier.fillMaxSize(),
            friendGroup = friendGroup
        )
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                state = rememberLazyListState(),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(friendList) {index, friend ->
                    FriendViewPagerItem(
                        friend = friend,
                        isShowFriendGroup = isShowFriendGroup,
                        onClickFriend = { onClickFriend(friend) }
                    )

                    if (index != friendList.lastIndex) {
                        HorizontalDivider()
                    }
                }

            }
        }
    }
}

@Composable
fun FriendViewPagerItem(
    modifier: Modifier = Modifier,
    friend: Friend,
    isShowFriendGroup: Boolean = false,
    onClickFriend: (Friend) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 16.dp)
            .clickable { onClickFriend(friend) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isShowFriendGroup) Arrangement.SpaceBetween else Arrangement.End
        ) {
            if (isShowFriendGroup) {
                FriendGroupChip(
                    text = friend.friendDetail.friendGroup.name,
                    chipColor = friend.friendDetail.friendGroup.color,
                    textColor = friend.friendDetail.friendGroup.color.getTextColorByBackgroundColor()
                )
            }

            Text(
                text = if (friend.friendDetail.birthday.isEmpty()) "" else friend.friendDetail.displayBirthdayOnlyDate(),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Text(
            text = friend.friendDetail.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
            text = friend.friendDetail.memo,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "💝 받은 선물",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = friend.receivedGiftCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "\uD83C\uDF81 준 선물",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
            Text(
                text = friend.sentGiftCount.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun EmptyFriendInGroup(
    modifier: Modifier = Modifier,
    friendGroup: FriendGroup,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = if (friendGroup == FriendGroup.allFriendGroup) {
                "등록된 친구가 존재하지 않습니다.\n 새로운 친구를 등록해보세요."
            } else {
                "${friendGroup.name} 그룹의 친구가 존재하지 않습니다.\n 새로운 친구를 등록해보세요."
            },
            style = MaterialTheme.typography.labelLarge,
        )
    }
}