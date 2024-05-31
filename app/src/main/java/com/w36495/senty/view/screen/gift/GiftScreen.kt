package com.w36495.senty.view.screen.gift

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.GiftViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun GiftScreen(
    vm: GiftViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val tabState = listOf(
        GiftTabState.ALL.title,
        GiftTabState.RECEIVED.title,
        GiftTabState.SENT.title
    )

    val pagerState = rememberPagerState(
        pageCount = tabState.size,
        initialOffscreenLimit = tabState.size,
        infiniteLoop = true,
        initialPage = GiftTabState.ALL.ordinal
    )
    LaunchedEffect(pagerState.currentPage) {
        when (pagerState.currentPage) {
            GiftTabState.ALL.ordinal -> { vm.getGifts() }
            GiftTabState.RECEIVED.ordinal -> { vm.getReceivedGifts() }
            GiftTabState.SENT.ordinal -> { vm.getSentGifts() }
        }
    }

    val gifts by vm.gifts.collectAsState()

    GiftContents(
        gifts = gifts,
        tabState = tabState,
        pagerState = pagerState,
        onBackPressed = { onBackPressed() },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
private fun GiftContents(
    gifts: List<Gift>,
    tabState: List<String>,
    pagerState: PagerState,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "선물목록") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            GiftTopTabLow(
                tabState = tabState,
                pagerState = pagerState,
            )

            Spacer(modifier = Modifier.height(4.dp))

            GiftHorizontalViewPager(
                modifier = Modifier.fillMaxWidth(),
                pagerState = pagerState,
                gifts = gifts,
                onClickGift = {}
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun GiftTopTabLow(
    tabState: List<String>,
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
                color = Green40
            )
        }
    ) {
        tabState.forEachIndexed { index, text ->
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
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun GiftHorizontalViewPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    gifts: List<Gift>,
    onClickGift: (String) -> Unit,
) {
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

            VerticalGrid(
                columns = SimpleGridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {

                gifts.forEach { gift ->
                    GiftViewPagerItem(
                        imgPath = gift.imgPath,
                        onClickGift = { onClickGift(gift.giftDetail.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun GiftViewPagerItem(
    modifier: Modifier = Modifier,
    imgPath: String = "",
    onClickGift: () -> Unit,
) {
    if (imgPath.isEmpty()) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .fillMaxWidth()
                .background(Color(0xFFD9D9D9))
                .clickable { onClickGift() }
        )
    } else {
        GlideImage(model = imgPath, contentDescription = null,
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(1f)
                .clickable { onClickGift() },
            contentScale = ContentScale.Crop
        )
    }
}

enum class GiftTabState(val title: String) {
    ALL("전체"), RECEIVED("받은 선물"), SENT("준 선물")
}