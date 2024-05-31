package com.w36495.senty.view.screen.gift

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import com.w36495.senty.view.entity.gift.GiftType
import com.w36495.senty.view.screen.home.friendEntityMock
import com.w36495.senty.view.screen.home.giftCategoryMock
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.buttons.SentyOutlinedButton
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyReadOnlyTextField
import com.w36495.senty.viewModel.GiftDetailViewModel

@Composable
fun GiftDetailScreen(
    vm: GiftDetailViewModel = hiltViewModel(),
    giftId: String,
    onBackPressed: () -> Unit,
    onClickEdit: (GiftDetail) -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.getGiftDetail(giftId)
    }

    val gift by vm.giftDetail.collectAsState()

    GiftDetailContents(
        giftDetail = giftDetail,
        onBackPressed = { onBackPressed() },
        onClickEdit = { onClickEdit(it) },
        onClickDelete = {
            vm.removeGift(giftId, giftDetail.gift.imgUri)
            onBackPressed()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftDetailContents(
    giftDetail: GiftDetail,
    onBackPressed: () -> Unit,
    onClickEdit: (GiftDetail) -> Unit,
    onClickDelete: (String) -> Unit,
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
            onComplete = { onClickDelete(gift.id) },
            onDismiss = { showDeleteDialog = false }
        )
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = if (gift.giftType == GiftType.SENT) "준 선물" else "받은 선물") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

            ImgSection(
                imgUri = giftDetail.imgPath,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            InfoSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                category = giftDetail.category,
                friend = giftDetail.friend,
                giftDetail = giftDetail.gift,
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
                    onClick = { onClickEdit(giftDetail) }
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ImgSection(
    modifier: Modifier = Modifier,
    imgUri: String,
) {
    if (imgUri.isEmpty()) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .background(Color(0xFFFBFBFB))
        )
    } else {
        GlideImage(
            model = imgUri, contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun InfoSection(
    modifier: Modifier = Modifier,
    category: GiftCategory,
    giftDetail: GiftDetailEntity,
    friend: FriendDetail,
) {
    Column(modifier = modifier) {
        InfoSectionItem(title = "카테고리", text = category.name)

        Spacer(modifier = Modifier.height(16.dp))

        InfoSectionItem(title = "날짜", text = giftDetail.date)

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

@Preview(showBackground = true, heightDp = 1228)
@Composable
private fun GiftDetailPreview() {
    SentyTheme {
        GiftDetailContents(
            onBackPressed = {},
            gift = GiftDetailEntity(
                friendId = "",
                categoryId = "생일",
                date = "2022-01-01",
                mood = "기분 최고!",
                memo = "",
            ),
            category = giftCategoryMock,
            friend = friendEntityMock,
            onClickEdit = {},
            onClickDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GiftDetailPreview2() {
    SentyTheme {
        GiftDetailContents(
            onBackPressed = {},
            gift = GiftDetailEntity(
                friendId = "",
                categoryId = "생일",
                date = "2022-01-01",
                mood = "기분 최고!",
                memo = "",
                giftType = GiftType.SENT
            ),
            category = giftCategoryMock,
            friend = friendEntityMock,
            onClickEdit = {},
            onClickDelete = {}
        )
    }
}