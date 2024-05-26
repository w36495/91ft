package com.w36495.senty.view.screen.friend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyElevatedButton
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.FriendDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun FriendDetailScreen(
    friendId: String,
    vm: FriendDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
    onClickEdit: (FriendDetail) -> Unit,
    onClickDelete: () -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.getFriend(friendId)
        vm.getGifts(friendId)
    }
    
    val friend by vm.friend.collectAsState()
    val gifts by vm.gifts.collectAsState()

    FriendDetailContents(
        friend = friend,
        onBackPressed = { onBackPressed() },
        onClickEdit = { onClickEdit(it) },
        onClickDelete = { onClickDelete() },
        gifts = gifts,
        onClickGiftDetail = { onClickGiftDetail(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendDetailContents(
    friend: FriendDetail,
    gifts: List<GiftDetailEntity>,
    onBackPressed: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
    onClickEdit: (FriendDetail) -> Unit,
    onClickDelete: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "친구") },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            FriendDetailViewPager(
                gifts = gifts,
                friend = friend,
                onClickGiftDetail = { onClickGiftDetail(it) },
                onClickDelete = { onClickDelete() },
                onClickEdit = { onClickEdit(it) },
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun FriendDetailViewPager(
    modifier: Modifier = Modifier,
    gifts: List<GiftDetailEntity>,
    friend: FriendDetail,
    onClickGiftDetail: (String) -> Unit,
    onClickEdit: (FriendDetail) -> Unit,
    onClickDelete: () -> Unit,
) {
    val tabData = listOf(
        FriendDetailTabState.INFORMATION.title,
        FriendDetailTabState.GIFT.title.plus("(${gifts.size})")
    )

    val pagerState = rememberPagerState(
        pageCount = tabData.size,
        initialOffscreenLimit = tabData.size,
        infiniteLoop = true,
        initialPage = FriendDetailTabState.GIFT.ordinal
    )
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = tabIndex,
        contentColor = Color.Black,
        containerColor = Color.White,
        indicator = {
            SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(it[tabIndex]),
                color = Green40
            )
        }
    ) {
        tabData.forEachIndexed { index, text ->
            Tab(
                selected = tabIndex == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { index ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
        ) {
            if (index == FriendDetailTabState.GIFT.ordinal) {
                Spacer(modifier = Modifier.height(24.dp))
                FriendInfoSection(friend = friend)

                BottomButtons(
                    onClickEdit = { onClickEdit(friend) },
                    onClickDelete = { onClickDelete() }
                )
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                GiftSection(
                    gifts = gifts,
                    onClickGift = { giftId ->
                        onClickGiftDetail(giftId)
                    }
                )
            }
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
private fun GiftSection(
    modifier: Modifier = Modifier,
    gifts: List<GiftDetailEntity>,
    onClickGift: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (gifts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFFBFBFB)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "등록된 선물이 없습니다.",
                        textAlign = TextAlign.Center,)
                }
            }
        } else {
            VerticalGrid(
                columns = SimpleGridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                gifts.forEach { gift ->
                    GiftItem(
                        gift = gift,
                        onClickGiftDetail = { onClickGift(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GiftItem(
    modifier: Modifier = Modifier,
    gift: GiftDetailEntity,
    onClickGiftDetail: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .background(Color.Gray)
            .clickable { onClickGiftDetail(gift.id) }
    )
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

@Preview(showBackground = true, heightDp = 1024)
@Composable
private fun FriendDetailPreview() {
    SentyTheme {
        FriendDetailContents(
            gifts = emptyList(),
            friend = FriendDetail(name = "철수", birthday = "1112", memo = ""),
            onClickDelete = {},
            onBackPressed = {},
            onClickEdit = {

            },
            onClickGiftDetail = {}
        )
    }
}

enum class FriendDetailTabState(val title: String) {
    INFORMATION("정보"), GIFT("선물")
}