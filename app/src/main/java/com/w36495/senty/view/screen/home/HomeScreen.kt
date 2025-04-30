package com.w36495.senty.view.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.util.dropShadow
import com.w36495.senty.view.component.SentyAsyncImage
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.screen.home.contact.HomeContact
import com.w36495.senty.view.screen.home.model.HomeGiftUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.cards.ScheduleWithDateList
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray80
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyPink60
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.SentyYellow60
import com.w36495.senty.view.ui.theme.antonFontFamily

@Composable
fun HomeRoute(
    vm: HomeViewModel = hiltViewModel(),
    padding: PaddingValues,
    moveToGifts: () -> Unit,
    moveToGiftDetail: (String) -> Unit,
) {
    val uiState by vm.state.collectAsStateWithLifecycle()
    val schedules by vm.schedules.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when(effect) {
                is HomeContact.Effect.ShowError -> {
                    
                }
                HomeContact.Effect.NavigateToGifts -> { moveToGifts() }
                is HomeContact.Effect.NavigateToGiftDetail -> { moveToGiftDetail(effect.giftId) }
            }
        }
    }

    HomeScreen(
        modifier = Modifier.padding(bottom = padding.calculateBottomPadding(), top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
        uiState = uiState,
        schedules = schedules,
        onClickGifts = { vm.handleEvent(HomeContact.Event.OnClickGifts) },
        onClickGiftDetail = { vm.handleEvent(HomeContact.Event.OnClickGift(it)) },
    )
}

@Composable
private fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeContact.State,
    schedules: List<Schedule>,
    onClickGifts: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name).uppercase(),
                    color = SentyWhite,
                    fontFamily = antonFontFamily,
                    fontSize = 16.sp,
                )
            },
            elevation = 0.dp,
            backgroundColor = SentyGreen60,
        )

        TopGiftButtons(
            sentGiftCount = uiState.sentGifts.size,
            receivedGiftCount = uiState.receivedGifts.size,
            onClickGifts = { onClickGifts() }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(SentyGreen60)
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                backgroundColor = Color.White,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(vertical = 32.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.home_anniversary_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = SentyTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (schedules.isEmpty()) EmptyGifts(stringResource(id = R.string.home_anniversary_empty_text))
                    else ScheduleWithDateList(schedules = schedules)

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = stringResource(id = R.string.home_gift_received_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = SentyTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        uiState.isReceivedGiftLoading -> {
                            LoadingGifts(loadingText = stringResource(id = R.string.home_gift_loading_text))
                        }
                        !uiState.isReceivedGiftLoading && uiState.receivedGifts.isEmpty() -> {
                            EmptyGifts(stringResource(id = R.string.home_gift_received_empty_text))
                        }
                        else -> {
                            GiftCardSection(
                                gifts = uiState.receivedGifts,
                                onClickAllGifts = { onClickGifts() },
                                onClickGiftDetail = { onClickGiftDetail(it) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = stringResource(id = R.string.home_gift_sent_title),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        style = SentyTheme.typography.titleLarge
                            .copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    when {
                        uiState.isSentGiftLoading -> {
                            LoadingGifts(loadingText = stringResource(id = R.string.home_gift_loading_text))
                        }
                        !uiState.isSentGiftLoading && uiState.sentGifts.isEmpty() -> {
                            EmptyGifts(stringResource(id = R.string.home_gift_sent_empty_text))
                        }
                        else -> {
                            GiftCardSection(
                                gifts = uiState.sentGifts,
                                onClickAllGifts = { onClickGifts() },
                                onClickGiftDetail = { onClickGiftDetail(it) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopGiftButtons(
    modifier: Modifier = Modifier,
    sentGiftCount: Int,
    receivedGiftCount: Int,
    onClickGifts: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(SentyGreen60)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .dropShadow(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x14000000),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blur = 4.dp,
                )
                .background(SentyWhite, RoundedCornerShape(10.dp))
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onClickGifts() },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.common_heart),
                    style = SentyTheme.typography.labelMedium
                        .copy(color = SentyPink60)
                )

                Text(
                    text = stringResource(id = R.string.common_received_gift_text),
                    style = SentyTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 4.dp),
                )

                Text(
                    text = "($receivedGiftCount)",
                    style = SentyTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
                .height(48.dp)
                .dropShadow(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x14000000),
                    offsetX = 0.dp,
                    offsetY = 0.dp,
                    blur = 4.dp,
                )
                .background(SentyWhite, RoundedCornerShape(10.dp))
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onClickGifts() },
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.common_heart),
                    style = SentyTheme.typography.labelMedium
                        .copy(color = SentyYellow60)
                )

                Text(
                    text = stringResource(id = R.string.common_sent_gift_text),
                    style = SentyTheme.typography.labelMedium,
                    modifier = Modifier.padding(start = 4.dp),
                )

                Text(
                    text = "($sentGiftCount)",
                    style = SentyTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyGifts(
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFFBFBFB), RoundedCornerShape(10.dp))
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 56.dp),
            text = text,
            style = SentyTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingGifts(
    loadingText: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFFBFBFB), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(
                color = SentyGreen60
            )

            Text(
                text = loadingText,
                style = SentyTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun GiftCardSection(
    modifier: Modifier = Modifier,
    gifts: List<HomeGiftUiModel>,
    onClickAllGifts: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(gifts) { index, gift ->
            if (index < 8) {
                GiftCardItem(
                    gift = gift,
                    onClickGiftDetail = { onClickGiftDetail(it) }
                )
            }

            if (index == 8) {
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable { onClickAllGifts() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    IconButton(
                        onClick = { },
                        enabled = false,
                        colors = IconButtonDefaults.iconButtonColors(
                            disabledContentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowCircleRight,
                            contentDescription = null,
                            tint = SentyGray80,
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.home_gift_show_all_text),
                        textAlign = TextAlign.Center,
                        style = SentyTheme.typography.bodyMedium
                            .copy(color = SentyGray80),
                    )
                }
            } else if (index != gifts.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }

        }
    }
}

@Composable
private fun GiftCardItem(
    modifier: Modifier = Modifier,
    gift: HomeGiftUiModel,
    onClickGiftDetail: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .width(156.dp)
            .dropShadow(
                shape = RoundedCornerShape(10.dp),
                offsetX = 0.dp,
                offsetY = 0.dp,
                blur = 2.dp
            )
            .background(
                color = SentyWhite,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClickGiftDetail(gift.id) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            gift.thumbnailPath?.let { path ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    SentyAsyncImage(
                        model = path,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                    )

                    if (gift.hasImageCount > 1) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 4.dp),
                            painter = painterResource(
                                id = if (gift.hasImageCount == 2) {
                                    R.drawable.ic_baseline_counter_2
                                } else {
                                    R.drawable.ic_baseline_counter_3
                                }
                            ),
                            contentDescription = null,
                            tint = SentyWhite
                        )
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(SentyGray10),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NoPhotography,
                        contentDescription = "Gift Image Empty",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar_month_24),
                        contentDescription = null,
                        tint = SentyGray80,
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = gift.date.replace("-", "."),
                        style = SentyTheme.typography.bodySmall
                            .copy(color = SentyGray80),
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = gift.friendName,
                    style = SentyTheme.typography.bodySmall
                        .copy(fontWeight = FontWeight.Medium),
                )
            }
        }
    }
}

//@Preview(showBackground = true, widthDp = 1080, heightDp = 1500)
//@Composable
//private fun HomeContentsPreview() {
//    HomeScreen(
//        sentGiftUiState = HomeGiftUiState.Success(
//            List(10) {
//                Gift(
//                    giftDetail = GiftDetail(
//                        category = GiftCategory(name = "생일"),
//                        friend = FriendUiModel2(
//                            name = "지수",
//                            birthday = "",
//                            memo = "",
//                        ),
//                        date = "2025-12-11",
//                        mood = "",
//                        memo = ""
//                    )
//                )
//            }
//        ),
//        receivedGiftUiState = HomeGiftUiState.Empty,
//        schedules = List(5) {
//                            Schedule(
//                                title = "기념일",
//                                date = "2025-04-24"
//                            )
//        },
////        schedules = emptyList(),
//        onClickGifts = {},
//        onClickGiftDetail = {},
//    )
//}

@Preview(showBackground = true)
@Composable
private fun LoadingGiftsPreview() {
    SentyTheme {
        LoadingGifts(
            loadingText = "선물을 불러오고 있습니다."
        )
    }
}