package com.w36495.senty.view.screen.friend.detail

import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.NoPhotography
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.w36495.senty.R
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.screen.friend.detail.contact.FriendDetailContact
import com.w36495.senty.view.screen.friend.detail.model.FriendDetailTabType
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.friendgroup.model.FriendGroupUiModel
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite
import kotlinx.coroutines.launch

@Composable
fun FriendDetailRoute(
    vm: FriendDetailViewModel = hiltViewModel(),
    friendId: String,
    moveToGiftDetail: (String) -> Unit,
    moveToEditFriend: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.getFriend(friendId)
    }

    val context = LocalContext.current
    val uiState by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                FriendDetailContact.Effect.NavigateToFriends -> {
                    onBackPressed()
                }
                is FriendDetailContact.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    vm.sendEffect(FriendDetailContact.Effect.NavigateToFriends)
                }
                is FriendDetailContact.Effect.ShowError -> {

                }
                is FriendDetailContact.Effect.NavigateToEditFriend -> {
                    moveToEditFriend(effect.friendId)
                }
                is FriendDetailContact.Effect.NavigateToGiftDetail -> {
                    moveToGiftDetail(effect.giftId)
                }
            }
        }
    }


    FriendUiModelContents(
        modifier = Modifier.navigationBarsPadding(),
        uiState = uiState,
        onBackPressed = { vm.handleEvent(FriendDetailContact.Event.OnClickBack) },
        onClickEdit = { vm.handleEvent(FriendDetailContact.Event.OnClickEdit(friendId)) },
        onClickGiftDetail = { vm.handleEvent(FriendDetailContact.Event.OnClickGift(it)) },
        onClickDelete = { vm.handleEvent(FriendDetailContact.Event.OnClickDelete) },
        onSelectDelete = { vm.handleEvent(FriendDetailContact.Event.OnSelectDelete(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FriendUiModelContents(
    modifier: Modifier = Modifier,
    uiState: FriendDetailContact.State,
    onBackPressed: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
    onSelectDelete: (String?) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.friend_detail_title),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            FriendDetailViewPager(
                modifier = Modifier.padding(innerPadding),
                gifts = uiState.gifts,
                friend = uiState.friend,
                onClickGiftDetail = onClickGiftDetail,
                onClickDelete = { onClickDelete() },
                onClickEdit = onClickEdit,
            )

            when {
                uiState.isLoading -> {
                    LoadingCircleIndicator(hasBackGround = false)
                }
                uiState.showDeleteDialog -> {
                    BasicAlertDialog(
                        title = stringResource(id = R.string.friend_detail_delete_title),
                        message = stringResource(id = R.string.friend_detail_delete_message_text),
                        hasCancel = true,
                        onDismiss = { onSelectDelete(null) },
                        onComplete = { onSelectDelete(uiState.friend.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FriendDetailViewPager(
    modifier: Modifier = Modifier,
    gifts: List<GiftUiModel>,
    friend: FriendUiModel,
    onClickGiftDetail: (String) -> Unit,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    val tabData = FriendDetailTabType.getTabs(giftCount = gifts.size)

    val pagerState = rememberPagerState(
        pageCount = { tabData.size },
        initialPage = FriendDetailTabType.INFORMATION.num,
    )

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            contentColor = SentyBlack,
            containerColor = SentyWhite,
            indicator = {
                SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(it[tabIndex]),
                    color = SentyGreen60,
                    height = 2.dp,
                )
            }
        ) {
            tabData.forEachIndexed { index, tab ->
                Tab(
                    selected = tabIndex == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                ) {
                    Text(
                        text = tab,
                        modifier = Modifier.padding(vertical = 16.dp),
                        style = if (tabIndex == index) SentyTheme.typography.titleMedium else SentyTheme.typography.bodyMedium,
                    )
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            if (index == FriendDetailTabType.INFORMATION.num) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp)
                            .padding(bottom = 88.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        FriendInfoSection(
                            friend = friend,
                        )
                    }

                    BottomButtons(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        onClickEdit = onClickEdit,
                        onClickDelete = { onClickDelete() }
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))

                GiftSection(
                    gifts = gifts,
                    onClickGift = onClickGiftDetail,
                )
            }
        }
    }
}

@Composable
private fun FriendInfoSection(
    modifier: Modifier = Modifier,
    friend: FriendUiModel,
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
                text = stringResource(id = R.string.friend_detail_name_text),
                style = SentyTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            SentyReadOnlyTextField(
                text = friend.name,
                textColor = SentyBlack,
                textStyle = SentyTheme.typography.bodyMedium,
            )
            Text(
                text = stringResource(id = R.string.friend_detail_group_text),
                style = SentyTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .padding(top = 32.dp)
            )

            SentyReadOnlyTextField(
                text = friend.groupName,
                group = FriendGroupUiModel(friend.groupId, friend.groupName, friend.groupColor),
                textColor = SentyBlack,
                textStyle = SentyTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(id = R.string.friend_detail_birthday_text),
                style = SentyTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp, top = 32.dp)
            )

            SentyReadOnlyTextField(
                text = if (friend.birthday.isEmpty()) "" else friend.displayBirthdayWithYear(),
                textColor = SentyBlack,
                textStyle = SentyTheme.typography.bodyMedium,
            )

            Text(
                text = stringResource(id = R.string.friend_detail_memo_text),
                style = SentyTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp, top = 32.dp)
            )

            SentyMultipleTextField(
                text = friend.memo,
                onChangeText = { },
                readOnly = true,
                textStyle = SentyTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun GiftSection(
    modifier: Modifier = Modifier,
    gifts: List<GiftUiModel>,
    onClickGift: (String) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if (gifts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBFBFB)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = SentyGray60,
                    )

                    Text(
                        text = stringResource(id = R.string.friend_detail_gift_empty_text),
                        textAlign = TextAlign.Center,
                        style = SentyTheme.typography.bodyMedium.copy(color = SentyGray60),
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        } else {
            VerticalGrid(
                columns = SimpleGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                gifts.forEach { gift ->
                    GiftItem(
                        gift = gift,
                        onClickGiftDetail = onClickGift
                    )
                }
            }
        }
    }
}

@Composable
private fun GiftItem(
    modifier: Modifier = Modifier,
    gift: GiftUiModel,
    onClickGiftDetail: (String) -> Unit,
) {
    val context = LocalContext.current

    if (gift.hasImages) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClickGiftDetail(gift.id) },
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(gift.images.first())
                    .size(200)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop,
            )

            if (gift.images.size > 1) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
                    painter = painterResource(
                        id = if (gift.images.size == 2) {
                            R.drawable.ic_baseline_counter_2
                        } else {
                            R.drawable.ic_baseline_counter_3
                        }
                    ),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFD9D9D9))
                .clickable { onClickGiftDetail(gift.id) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.NoPhotography,
                contentDescription = "Gift Image Empty",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun BottomButtons(
    modifier: Modifier = Modifier,
    onClickEdit: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SentyFilledButton(
            text = stringResource(id = R.string.common_edit),
            modifier = Modifier.weight(1f)
        ) { onClickEdit() }

        SentyOutlinedButton(
            text = stringResource(id = R.string.common_remove),
            modifier = Modifier.weight(1f)
        ) { onClickDelete() }
    }
}



@Preview(showBackground = true, heightDp = 400)
@Composable
private fun FriendUiModelViewPagerPreview() {
    SentyTheme {
        FriendDetailViewPager(
            gifts = emptyList(),
            friend = FriendUiModel(
                name = "친구1",
                birthday = "2025-10-10",
                memo = "",
            ),
            onClickGiftDetail = {},
            onClickEdit = {},
            onClickDelete = {}
        )
    }
}