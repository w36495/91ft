package com.w36495.senty.view.screen.gift

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.NoPhotography
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.entity.gift.GiftType
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.viewModel.GiftDetailViewModel
import kotlin.math.absoluteValue

@Composable
fun GiftDetailScreen(
    vm: GiftDetailViewModel = hiltViewModel(),
    giftId: String,
    onBackPressed: () -> Unit,
    onClickEdit: (String) -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.getGift(giftId)
    }

    val gift by vm.gift.collectAsStateWithLifecycle()

    GiftDetailContents(
        gift = gift,
        onBackPressed = { onBackPressed() },
        onClickEdit = {
            onClickEdit(it)
        },
        onClickDelete = {
            vm.removeGift(gift)
            onBackPressed()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftDetailContents(
    gift: Gift,
    onBackPressed: () -> Unit,
    onClickEdit: (String) -> Unit,
    onClickDelete: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        BasicAlertDialog(
            title = "삭제하시겠습니까?",
            discContent = {
                Text(
                    text = "삭제된 선물은 복구가 불가능합니다.",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
            },
            onComplete = { onClickDelete() },
            onDismiss = { showDeleteDialog = false }
        )
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = if (gift.giftDetail.giftType == GiftType.SENT) "준 선물" else "받은 선물") },
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
        },
        containerColor = Color.White
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                ImgSection(
                    imgUri = gift.giftImages,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                InfoSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    category = gift.giftDetail.category,
                    friend = gift.giftDetail.friend,
                    giftDetail = gift.giftDetail,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    SentyFilledButton(
                        text = "수정",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickEdit(gift.giftDetail.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SentyOutlinedButton(
                        text = "삭제",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showDeleteDialog = true }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun ImgSection(
    modifier: Modifier = Modifier,
    imgUri: List<Any>,
) {
    if (imgUri.isEmpty()) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .background(Color(0xFFFBFBFB)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.NoPhotography,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    } else {
        ImagePager(
            modifier = Modifier.fillMaxWidth(),
            imgUri = imgUri
        )
    }
}

@Composable
private fun ImagePager(
    modifier: Modifier = Modifier,
    imgUri: List<Any>,
) {
    val pagerState = rememberPagerState(pageCount = imgUri.size)

    Column(modifier = modifier) {
        HorizontalPager(state = pagerState) {page ->
            AsyncImage(
                model = imgUri[page],
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .aspectRatio(1f)
                    .graphicsLayer {
                        val pageOffset = (
                                (pagerState.currentPage - page) + pagerState.currentPage
                                ).absoluteValue

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0, 1)
                        )
                    }
            )
        }

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray
                else Color.LightGray

                Box(modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(color)
                    .size(8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImgSectionPreview() {
    SentyTheme {
        Column {
            ImgSection(imgUri = emptyList())

            ImgSection(imgUri = listOf(1, 2, 3))
        }
    }
}

@Composable
private fun InfoSection(
    modifier: Modifier = Modifier,
    category: GiftCategory,
    giftDetail: GiftDetail,
    friend: FriendDetail,
) {
    Column(modifier = modifier) {
        InfoSectionItem(title = "카테고리", text = category.name)

        Spacer(modifier = Modifier.height(16.dp))

        InfoSectionItem(
            title = "날짜",
            text = if (giftDetail.date.isNotEmpty()) {
                val (year, month, day) = giftDetail.date.split("-").map { it.toInt() }
                "${year}년 ${StringUtils.format2Digits(month)}월 ${StringUtils.format2Digits(day)}일"
            } else ""
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoSectionItem(title = "친구", text = friend.name)

        Spacer(modifier = Modifier.height(16.dp))

        InfoSectionItem(title = "기분", text = giftDetail.mood)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "메모",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        SentyMultipleTextField(
            text = giftDetail.memo,
            onChangeText = { },
            readOnly = true
        )
    }
}

@Composable
private fun InfoSectionItem(
    title: String,
    text: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    SentyReadOnlyTextField(
        text = text,
        textColor = MaterialTheme.colorScheme.onSurface
    )
}