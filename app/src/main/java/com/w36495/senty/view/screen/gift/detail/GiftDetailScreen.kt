package com.w36495.senty.view.screen.gift.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.w36495.senty.R
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.component.SentyAsyncImage
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.gift.detail.contact.GiftDetailContact
import com.w36495.senty.view.screen.gift.model.GiftUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.view.ui.theme.SentyBlack

@Composable
fun GiftDetailRoute(
    vm: GiftDetailViewModel = hiltViewModel(),
    padding: PaddingValues,
    giftId: String,
    moveToGiftEdit: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.getGift(giftId)
    }

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is GiftDetailContact.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    vm.sendEffect(GiftDetailContact.Effect.NavigateToBack)
                }
                is GiftDetailContact.Effect.ShowError -> {

                }
                is GiftDetailContact.Effect.NavigateToEditGift -> {
                    moveToGiftEdit(effect.giftId)
                }
                GiftDetailContact.Effect.NavigateToBack -> {
                    onBackPressed()
                }
            }
        }
    }

    GiftDetailContents(
        uiState = uiState,
        onBackPressed = { vm.handleEvent(GiftDetailContact.Event.OnClickBack) },
        onClickEdit = { vm.handleEvent(GiftDetailContact.Event.OnClickEdit(it)) },
        onClickDelete = { vm.handleEvent(GiftDetailContact.Event.OnClickDelete) },
        onSelectDelete = { vm.handleEvent(GiftDetailContact.Event.OnSelectDelete(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftDetailContents(
    uiState: GiftDetailContact.State,
    onBackPressed: () -> Unit,
    onClickEdit: (String) -> Unit,
    onClickDelete: () -> Unit,
    onSelectDelete: (String?) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = if (uiState.gift.type == GiftType.RECEIVED) R.string.gift_detail_received_title else R.string.gift_detail_sent_title),
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
        containerColor = Color.White
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ImgSection(
                    imgUri = uiState.gift.images,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                InfoSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    category = GiftCategoryUiModel(id = uiState.gift.categoryId, name = uiState.gift.categoryName),
                    friend = FriendUiModel(id = uiState.gift.friendId, name = uiState.gift.friendName),
                    giftDetail = uiState.gift,
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    SentyFilledButton(
                        text = stringResource(id = R.string.common_edit),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onClickEdit(uiState.gift.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SentyOutlinedButton(
                        text = stringResource(id = R.string.common_remove),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onClickDelete,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            when {
                uiState.isLoading -> {
                    LoadingCircleIndicator(hasBackGround = false)
                }
                uiState.showDeleteDialog -> {
                    BasicAlertDialog(
                        title = stringResource(id = R.string.gift_detail_delete_title),
                        message = stringResource(id = R.string.gift_detail_delete_message_text),
                        hasCancel = true,
                        onComplete = { onSelectDelete(uiState.gift.id) },
                        onDismiss = { onSelectDelete(null) }
                    )
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
    val pagerState = rememberPagerState(
        pageCount = { imgUri.size },
    )

    Column(modifier = modifier) {
        HorizontalPager(state = pagerState) {page ->
            SentyAsyncImage(
                model = imgUri[page],
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
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
    category: GiftCategoryUiModel,
    giftDetail: GiftUiModel,
    friend: FriendUiModel,
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
            style = SentyTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        SentyMultipleTextField(
            text = giftDetail.memo,
            onChangeText = { },
            readOnly = true,
            textStyle = SentyTheme.typography.bodyMedium
                .copy(color = SentyBlack),
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
        style = SentyTheme.typography.labelSmall,
        modifier = Modifier.padding(bottom = 4.dp)
    )

    SentyReadOnlyTextField(
        text = text,
        textStyle = SentyTheme.typography.bodyMedium
            .copy(color = SentyBlack),
    )
}