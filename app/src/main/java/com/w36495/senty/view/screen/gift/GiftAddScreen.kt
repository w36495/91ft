package com.w36495.senty.view.screen.gift

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.w36495.senty.util.StringUtils
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.entity.gift.GiftDetailEntity
import com.w36495.senty.view.entity.gift.GiftType
import com.w36495.senty.view.screen.friend.FriendDialogScreen
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.dialogs.BasicCalendarDialog
import com.w36495.senty.view.ui.component.dialogs.ImageSelectionDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.Green40
import com.w36495.senty.viewModel.GiftAddViewModel

@Composable
fun GiftAddScreen(
    vm: GiftAddViewModel = hiltViewModel(),
    giftDetail: GiftDetail?,
    onPressedBack: () -> Unit,
    onComplete: () -> Unit,
) {
    var showImageSelectionDialog by remember { mutableStateOf(false) }
    var giftImg: Any? by remember { mutableStateOf(null) }

    val takePhotoFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri -> giftImg = uri }
            }
        }

    val takePhotoFromCamera =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) {
            if (it != null) { giftImg = it }
        }

    val takePhotoFromGalleryIntent =
        Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png", "image/bmp", "image/webp")
            )
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

    if (showImageSelectionDialog) {
        ImageSelectionDialog(
            onDismiss = { showImageSelectionDialog = false },
            onClickCamera = { takePhotoFromCamera.launch() },
            onClickGallery = { takePhotoFromGallery.launch(takePhotoFromGalleryIntent) }
        )
    }

    GiftAddContents(
        giftDetail = if (giftDetail == GiftDetail.emptyGiftDetail) null else giftDetail,
        giftImg = giftImg,
        onPressedBack = { onPressedBack() },
        onClickSave = {
            if (giftDetail == null) vm.saveGift(it, giftImg)
            else vm.updateGift(it, giftImg)
            onComplete()
        },
        onClickUploadImage = { showImageSelectionDialog = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftAddContents(
    giftDetail: GiftDetail?,
    giftImg: Any? = null,
    onPressedBack: () -> Unit,
    onClickSave: (GiftDetailEntity) -> Unit,
    onClickUploadImage: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = if (giftDetail == null) "선물등록" else "선물수정")
                },
                navigationIcon = {
                    IconButton(onClick = { onPressedBack() }) {
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
        },
        backgroundColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ImgSection(
                giftImg = giftDetail?.imgPath ?: giftImg,
                modifier = Modifier.fillMaxWidth(),
                takePhotoLauncher = { onClickUploadImage() }
            )
            InputSection(
                giftDetail = giftDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClickSave = {
                    onClickSave(it)
                },
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ImgSection(
    modifier: Modifier = Modifier,
    giftImg: Any? = null,
    takePhotoLauncher: () -> Unit
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF6F6F6))
            .aspectRatio(1f)
            .clickable { takePhotoLauncher() },
        contentAlignment = Alignment.Center
    ) {
        if (giftImg != null) {
            GlideImage(
                model = giftImg, contentDescription = null,
                modifier = Modifier.aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = null)
                Text(
                    text = "사진 등록",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun InputSection(
    giftDetail: GiftDetail?,
    modifier: Modifier = Modifier,
    onClickSave: (GiftDetailEntity) -> Unit,
) {
    var showGiftCategoryDialog by remember { mutableStateOf(false) }
    var showFriendsDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    var type by remember { mutableStateOf(giftDetail?.gift?.giftType ?: GiftType.RECEIVED) }
    var category by remember { mutableStateOf(giftDetail?.category ?: GiftCategory.emptyCategory) }
    var friend by remember { mutableStateOf(giftDetail?.friend ?: FriendDetail.emptyFriendEntity) }
    var date by remember { mutableStateOf(giftDetail?.gift?.date ?: "") }
    var mood by remember { mutableStateOf(giftDetail?.gift?.mood ?: "") }
    var memo by remember { mutableStateOf(giftDetail?.gift?.memo ?: "") }

    if (showGiftCategoryDialog) {
        GiftCategoryDialogScreen(
            onDismiss = { showGiftCategoryDialog = false },
            onClickCategory = {
                category = it
                showGiftCategoryDialog = false
            }
        )
    } else if (showFriendsDialog) {
        FriendDialogScreen(
            onDismiss = { showFriendsDialog = false },
            onClickFriend = {
                friend = it.toFriendDetail()
                showFriendsDialog = false
            }
        )
    } else if (showDatePickerDialog) {
        BasicCalendarDialog(
            onDismiss = { showDatePickerDialog = false },
            onSelectedDate = { year, month, day ->
                date = "$year-${StringUtils.format2Digits(month + 1)}-${StringUtils.format2Digits(day)}"
            }
        )
    }

    Column(modifier = modifier.padding(top = 16.dp)) {
        GiftTypeSection(
            modifier = Modifier.fillMaxWidth(),
            type = type,
            onChangeToReceived = {
                type = GiftType.RECEIVED
            },
            onChangeToSent = {
                type = GiftType.SENT
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = "카테고리",
            enable = false,
            text = category.name,
            placeHolder = giftDetail?.category?.name ?: "카테고리를 입력해주세요.",
            onChangeText = { },
            onClick = { showGiftCategoryDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = "친구",
            text = friend.name,
            placeHolder = giftDetail?.friend?.name ?: "친구를 선택해주세요.",
            onChangeText = { },
            enable = false,
            onClick = { showFriendsDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = "날짜",
            text = if (date == "") {
                date
            } else {
                String.format(
                    java.util.Locale.KOREA,
                    "%04d년 %02d월 %02d일",
                    date.split("-")[0].toInt(),
                    date.split("-")[1].toInt(),
                    date.split("-")[2].toInt()
                )
            },
        enable = false,
        placeHolder = if (giftDetail == null) "날짜를 입력해주세요." else date,
        onChangeText = { date = it },
        onClick = { showDatePickerDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = "기분",
            text = mood,
            placeHolder = if (giftDetail == null || giftDetail.gift.mood.isEmpty()) "기분을 입력해주세요." else mood,
            onChangeText = { mood = it },
            onClick = {}
        )

        Text(
            text = "메모", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp, top = 16.dp)
        )
        SentyMultipleTextField(
            text = memo,
            onChangeText = { memo = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        SentyFilledButton(
            text = if (giftDetail == null) "등록" else "수정",
            modifier = Modifier.fillMaxWidth()
        ) {
            val giftDetailEntity = GiftDetailEntity(
                categoryId = category.id,
                friendId = friend.id,
                date = date,
                mood = mood,
                memo = memo,
                giftType = type,
            )

            if (giftDetail != null) giftDetailEntity.setId(giftDetail.gift.id)

            onClickSave(giftDetailEntity)
        }
    }
}

@Composable
private fun TextSection(
    modifier: Modifier = Modifier,
    title: String,
    text: String,
    placeHolder: String,
    enable: Boolean = true,
    onChangeText: (String) -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        SentyTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            hint = placeHolder,
            errorMsg = "",
            onChangeText = {
                onChangeText(it)
            },
            enabled = enable
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GiftTypeSection(
    modifier: Modifier = Modifier,
    type: GiftType = GiftType.RECEIVED,
    onChangeToReceived: () -> Unit,
    onChangeToSent: () -> Unit,
) {
    Row(modifier = modifier) {
        Chip(
            modifier = Modifier.weight(1f),
            colors = ChipDefaults.chipColors(
                backgroundColor = if (type == GiftType.RECEIVED) Green40 else Color.White
            ),
            border = BorderStroke(1.dp, Green40),
            shape = RoundedCornerShape(10.dp),
            onClick = { onChangeToReceived() },
        ) {
            Text(
                text = "받은 선물",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = if (type == GiftType.RECEIVED) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Chip(
            modifier = Modifier.weight(1f),
            colors = ChipDefaults.chipColors(
                backgroundColor = if (type == GiftType.SENT) Green40 else Color.White
            ),
            border = BorderStroke(1.dp, Green40),
            shape = RoundedCornerShape(10.dp),
            onClick = { onChangeToSent() },
        ) {
            Text(
                text = "준 선물",
                color = if (type == GiftType.SENT) Color.White else Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImgSectionPreview() {
    SentyTheme {
        ImgSection {

        }
    }
}