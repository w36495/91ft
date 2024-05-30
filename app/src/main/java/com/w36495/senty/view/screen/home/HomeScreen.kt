package com.w36495.senty.view.screen.home

import android.net.Uri
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import com.w36495.senty.view.entity.gift.GiftEntity
import com.w36495.senty.view.entity.gift.GiftType
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.cards.ScheduleWithDateList
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onClickGiftButton: () -> Unit,
) {
    val sentGifts by vm.sentGifts.collectAsState()
    val receivedGifts by vm.receivedGifts.collectAsState()
    val schedules by vm.schedules.collectAsState()

    HomeContents(
        sentGifts = sentGifts,
        receivedGifts = receivedGifts,
        schedules = schedules,
        onClickGiftButton = { onClickGiftButton() }
    )
}

@Composable
private fun HomeContents(
    sentGifts: List<GiftEntity>,
    receivedGifts: List<GiftEntity>,
    schedules: List<Schedule>,
    onClickGiftButton: () -> Unit,
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
                    style = MaterialTheme.typography.titleMedium
                )
            },
            elevation = 0.dp,
            backgroundColor = Green40
        )

        TopGiftButtons(
            sentGiftCount = sentGifts.size,
            receivedGiftCount = receivedGifts.size,
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

                    if (receivedGifts.isEmpty()) EmptyGifts("받은 선물을 등록해보세요.")
                    else GiftCardSection(
                        gifts = receivedGifts,
                        onClickAllGifts = { onClickGiftButton() }
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Text(
                        text = "준 선물",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (sentGifts.isEmpty()) EmptyGifts("준 선물을 등록해보세요.")
                    else GiftCardSection(
                        gifts = sentGifts,
                        onClickAllGifts = { onClickGiftButton() }
                    )
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
private fun GiftCardSection(
    modifier: Modifier = Modifier,
    gifts: List<GiftEntity>,
    onClickAllGifts: () -> Unit,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(gifts) { index, gift ->
            GiftCardItem(giftImg = gift.giftImg, gift = gift.gift, friend = gift.friend)
            if (index < 8) {
                GiftCardItem(giftImg = gift.giftImg, gift = gift.gift, friend = gift.friend)
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
                        onClick = {  },
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
    giftImg: String,
    gift: GiftDetailEntity,
    friend: FriendDetail,
) {
    Card(
        modifier = modifier.width(156.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp,
        onClick = { /*TODO*/ }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (giftImg.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color(0xFFD9D9D9))
                )
            } else {
                GlideImage(
                    model = Uri.parse(giftImg),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                )
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