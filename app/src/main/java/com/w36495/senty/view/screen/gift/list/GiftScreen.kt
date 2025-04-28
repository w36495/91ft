package com.w36495.senty.view.screen.gift.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.w36495.senty.R
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.component.SentyAsyncImage
import com.w36495.senty.view.screen.gift.list.contact.GiftContact
import com.w36495.senty.view.screen.gift.list.contact.GiftListUiModel
import com.w36495.senty.view.screen.gift.list.model.GiftTabType
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray40
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGray80
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite
import kotlinx.coroutines.launch

@Composable
fun GiftRoute(
    vm: GiftViewModel = hiltViewModel(),
    padding: PaddingValues,
    moveToGiftDetail: (String) -> Unit,
    moveToGiftCategories: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val uiState by vm.state.collectAsStateWithLifecycle()

    val tabList = GiftTabType.entries.toList()

    val pagerState = rememberPagerState(
        initialPage = GiftTabType.ALL.num,
        pageCount = { tabList.size },
    )

    LaunchedEffect(pagerState.currentPage) {
        vm.handleEvent(GiftContact.Event.OnSelectTab(pagerState.currentPage))
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is GiftContact.Effect.ShowToast -> {

                }
                is GiftContact.Effect.ShowError -> {

                }
                GiftContact.Effect.NavigateToBack -> { onBackPressed() }
                GiftContact.Effect.NavigateToGiftCategories -> { moveToGiftCategories() }
                is GiftContact.Effect.NavigateToGiftDetail -> { moveToGiftDetail(effect.giftId) }
            }
        }
    }

    GiftContents(
        modifier = Modifier.navigationBarsPadding(),
        uiState = uiState,
        tabList = tabList,
        pagerState = pagerState,
        onBackPressed = { vm.handleEvent(GiftContact.Event.OnClickBack) },
        onClickGiftCategories = { vm.handleEvent(GiftContact.Event.OnClickGiftCategories) },
        onClickGiftDetail = { vm.handleEvent(GiftContact.Event.OnClickGift(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftContents(
    modifier: Modifier = Modifier,
    uiState: GiftContact.State,
    tabList: List<GiftTabType>,
    pagerState: PagerState,
    onClickGiftDetail: (String) -> Unit,
    onClickGiftCategories: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.gift_title),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { onClickGiftCategories() }) {
                        Icon(imageVector = Icons.Outlined.Settings, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SentyWhite)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                GiftTopTabLow(
                    tabList = tabList,
                    pagerState = pagerState,
                )

                Spacer(modifier = Modifier.height(4.dp))

                GiftHorizontalViewPager(
                    modifier = Modifier.fillMaxWidth(),
                    pagerState = pagerState,
                    gifts = uiState.gifts,
                    isLoading = uiState.isLoading,
                    onClickGift = onClickGiftDetail
                )
            }

            if (uiState.isLoading) {
                LoadingCircleIndicator(hasBackGround = false)
            }
        }
    }
}

@Composable
private fun GiftTopTabLow(
    tabList: List<GiftTabType>,
    pagerState: PagerState,
) {
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = tabIndex,
        contentColor = Color.Black,
        containerColor = Color.White,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(it[tabIndex]),
                color = SentyGreen60
            )
        }
    ) {
        tabList.forEachIndexed { index, tab ->
            Tab(
                selected = tabIndex == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            ) {
                Text(
                    text = stringResource(id = tab.title),
                    modifier = Modifier.padding(
                        vertical = 12.dp,
                        horizontal = 16.dp
                    ),
                    style = if (tabIndex == index) SentyTheme.typography.titleMedium else SentyTheme.typography.bodyMedium,
                    color = SentyGray80
                )
            }
        }
    }
}

@Composable
private fun GiftHorizontalViewPager(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    gifts: List<GiftListUiModel>,
    pagerState: PagerState,
    onClickGift: (String) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        if (gifts.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFBFBFB)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(id = when (page) {
                        0 -> R.string.gift_empty_text_all
                        1 -> R.string.gift_empty_text_received
                        else -> R.string.gift_empty_text_sent
                    }),
                    textAlign = TextAlign.Center,
                    style = SentyTheme.typography.labelMedium.copy(color = SentyGray60),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
            ) {
                VerticalGrid(
                    columns = SimpleGridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    gifts.forEach { gift ->
                        GiftViewPagerItem(
                            thumbnail = gift.thumbnailPath,
                            hasImageCount = gift.hasImageCount,
                            onClickGift = { onClickGift(gift.id) }
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun GiftViewPagerItem(
    modifier: Modifier = Modifier,
    thumbnail: String?,
    hasImageCount: Int,
    onClickGift: () -> Unit,
) {
    thumbnail?.let { path ->
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClickGift() },
        ) {
            SentyAsyncImage(
                model = path,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
            )

            if (hasImageCount > 1) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp),
                    painter = painterResource(
                        id = if (hasImageCount == 2) {
                            R.drawable.ic_baseline_counter_2
                        } else {
                            R.drawable.ic_baseline_counter_3
                        }
                    ),
                    contentDescription = null,
                    tint = SentyWhite,
                )
            }
        }
    } ?: run {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(SentyGray10)
                .clickable { onClickGift() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.NoPhotography,
                contentDescription = "Gift Image Empty",
                tint = SentyGray40
            )
        }
    }
}