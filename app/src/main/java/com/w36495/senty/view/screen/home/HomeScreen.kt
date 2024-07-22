package com.w36495.senty.view.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.w36495.senty.R
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.cards.ScheduleWithDateList
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.HomeGiftUiState
import com.w36495.senty.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onClickGiftButton: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
) {
    val sentGifts by vm.sentGifts.collectAsStateWithLifecycle()
    val receivedGifts by vm.receivedGifts.collectAsStateWithLifecycle()
    val schedules by vm.schedules.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = sentGifts) {
        vm.getSentGifts()
    }

    LaunchedEffect(key1 = receivedGifts) {
        vm.getReceivedGifts()
    }

    LaunchedEffect(key1 = schedules) {
        vm.getSchedules()
    }

    HomeContents(
        sentGiftUiState = sentGifts,
        receivedGiftUiState = receivedGifts,
        schedules = schedules,
        onClickGiftButton = { onClickGiftButton() },
        onClickGiftDetail = { onClickGiftDetail(it) },
    )
}

@Composable
private fun HomeContents(
    sentGiftUiState: HomeGiftUiState,
    receivedGiftUiState: HomeGiftUiState,
    schedules: List<Schedule>,
    onClickGiftButton: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Senty",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            },
            elevation = 0.dp,
            backgroundColor = Green40
        )

        TopGiftButtons(
            sentGiftCount = if (sentGiftUiState is HomeGiftUiState.Success) sentGiftUiState.gifts.size else 0,
            receivedGiftCount = if (receivedGiftUiState is HomeGiftUiState.Success) receivedGiftUiState.gifts.size else 0,
            onClickGiftButton = { onClickGiftButton() }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Green40)
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
                        text = "다가오는 기념일",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (schedules.isEmpty()) EmptyGifts("기념일을 등록해보세요.")
                    else ScheduleWithDateList(schedules = schedules)

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "받은 선물",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when (receivedGiftUiState) {
                        HomeGiftUiState.Loading -> {
                            LoadingGifts()
                        }

                        HomeGiftUiState.Empty -> {
                            EmptyGifts("받은 선물을 등록해보세요.")
                        }

                        is HomeGiftUiState.Success -> {
                            GiftCardSection(
                                gifts = receivedGiftUiState.gifts,
                                onClickAllGifts = { onClickGiftButton() },
                                onClickGiftDetail = { onClickGiftDetail(it) },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "준 선물",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when (sentGiftUiState) {
                        HomeGiftUiState.Loading -> {
                            LoadingGifts()
                        }

                        HomeGiftUiState.Empty -> {
                            EmptyGifts("준 선물을 등록해보세요.")
                        }

                        is HomeGiftUiState.Success -> {
                            GiftCardSection(
                                gifts = sentGiftUiState.gifts,
                                onClickAllGifts = { onClickGiftButton() },
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
    onClickGiftButton: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(Green40)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 32.dp)
    ) {
        SentyFilledButton(
            modifier = Modifier.weight(1f),
            buttonColor = Color.White,
            textColor = Color.Black,
            text = "받은 선물 $receivedGiftCount",
            onClick = { onClickGiftButton() }
        )

        Spacer(modifier = Modifier.width(8.dp))

        SentyFilledButton(
            modifier = Modifier.weight(1f),
            buttonColor = Color.White,
            textColor = Color.Black,
            text = "준 선물 $sentGiftCount",
            onClick = { onClickGiftButton() }
        )
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
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingGifts() {
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
                color = Green40
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "선물을 불러오고 있습니다.",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun GiftCardSection(
    modifier: Modifier = Modifier,
    gifts: List<Gift>,
    onClickAllGifts: () -> Unit,
    onClickGiftDetail: (String) -> Unit,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(gifts) { index, gift ->
            if (index < 8) {
                GiftCardItem(
                    giftImages = gift.giftImages,
                    gift = gift.giftDetail,
                    friend = gift.giftDetail.friend,
                    onClickGiftDetail = { onClickGiftDetail(it) }
                )
            }

            if (index == 8) {
                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable { onClickAllGifts() },
                    verticalArrangement = Arrangement.Center,
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
                            contentDescription = null
                        )
                    }
                    Text(
                        text = "선물\n전체보기",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else if (index != gifts.lastIndex) {
                Spacer(modifier = Modifier.width(8.dp))
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalGlideComposeApi::class)
@Composable
private fun GiftCardItem(
    modifier: Modifier = Modifier,
    giftImages: List<Any>,
    gift: GiftDetail,
    friend: FriendDetail,
    onClickGiftDetail: (String) -> Unit,
) {
    Card(
        modifier = modifier.width(156.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp,
        onClick = { onClickGiftDetail(gift.id) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (giftImages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFFD9D9D9)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.NoPhotography,
                        contentDescription = "Gift Image Empty",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                ) {
                    GlideImage(
                        model = giftImages[0],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Crop,
                    ) {
                        it.override(200)
                    }

                    if (giftImages.size > 1) {
                        Icon(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 4.dp, end = 4.dp),
                            painter = painterResource(
                                id = if (giftImages.size == 2) {
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
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "🗓️ ${gift.date.replace("-", "/")}",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}