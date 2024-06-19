package com.w36495.senty.view.screen.gift

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.w36495.senty.util.StringUtils
import com.w36495.senty.util.checkCameraPermission
import com.w36495.senty.util.getUriFile
import com.w36495.senty.view.entity.FriendDetail
import com.w36495.senty.view.entity.gift.Gift
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.entity.gift.GiftDetail
import com.w36495.senty.view.entity.gift.GiftType
import com.w36495.senty.view.screen.friend.FriendDialogScreen
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
    giftId: String?,
    onPressedBack: () -> Unit,
    onComplete: () -> Unit,
) {
    if (giftId != null) {
        LaunchedEffect(Unit) {
            vm.getGift(giftId)
        }
    }

    val gift by vm.gift.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        vm.snackbarMsg.collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    val context = LocalContext.current
    var tempImageUri by remember { mutableStateOf(Uri.EMPTY) }

    val takePhotoFromCamera =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) { vm.setGiftImg(tempImageUri) }
        }

    val launcherCameraPermission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            tempImageUri = context.getUriFile()
            takePhotoFromCamera.launch(tempImageUri)
        } else {
            vm.writeSnackMsg("카메라 권한이 거부되어있습니다.")
        }
    }

    GiftAddContents(
        gift = if (giftId == null) null else gift,
        selectGiftImg = vm.giftImg.value,
        setGiftImg = { vm.setGiftImg(it) },
        deleteGiftImg = { vm.resetGiftImg() },
        snackbarHostState = snackbarHostState,
        onPressedBack = { onPressedBack() },
        onClickCamera = {
            val hasPermission = context.checkCameraPermission(arrayOf(Manifest.permission.CAMERA))

            if (!hasPermission) {
                launcherCameraPermission.launch(Manifest.permission.CAMERA)
            } else {
                tempImageUri = context.getUriFile()
                takePhotoFromCamera.launch(tempImageUri)
            }
        },
        onClickSave = { giftDetail ->
            if (giftId == null) {
                if (vm.validateGift(giftDetail)) {
                    vm.saveGift(giftDetail)

                    onComplete()
                }
            } else {
                if (vm.validateGift(giftDetail)) {
                    vm.updateGift(giftDetail)

                    onComplete()
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftAddContents(
    gift: Gift?,
    selectGiftImg: Any?,
    setGiftImg: (Any) -> Unit,
    deleteGiftImg: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onPressedBack: () -> Unit,
    onClickCamera: () -> Unit,
    onClickSave: (GiftDetail) -> Unit,
) {
    var showImageSelectionDialog by remember { mutableStateOf(false) }

    val takePhotoFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri -> setGiftImg(uri) }
            }
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
            hasImagePath = gift?.giftImg != null,
            onDismiss = { showImageSelectionDialog = false },
            onClickCamera = onClickCamera,
            onClickGallery = { takePhotoFromGallery.launch(Intent.createChooser(takePhotoFromGalleryIntent, "Select Picture")) },
            onClickDelete = {
                deleteGiftImg()
                showImageSelectionDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = gift?.giftDetail?.let { "선물수정" } ?: "선물등록")
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
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        backgroundColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ImgSection(
                giftImg = gift?.giftImg ?: selectGiftImg,
                modifier = Modifier.fillMaxWidth(),
                takePhotoLauncher = { showImageSelectionDialog = true }
            )
            InputSection(
                giftDetail = gift?.giftDetail,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                onClickSave = { detail ->
                    onClickSave(detail)
                },
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun ImgSection(
    modifier: Modifier = Modifier,
    giftImg: Any?,
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
                model = giftImg,
                contentDescription = null,
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
    onClickSave: (GiftDetail) -> Unit,
) {
    var showGiftCategoryDialog by remember { mutableStateOf(false) }
    var showFriendsDialog by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    var type by remember(giftDetail?.id) { mutableStateOf(giftDetail?.giftType ?: GiftType.RECEIVED) }
    var category by remember(giftDetail?.id) { mutableStateOf(giftDetail?.category ?: GiftCategory.emptyCategory) }
    var friend by remember(giftDetail?.id) { mutableStateOf(giftDetail?.friend ?: FriendDetail.emptyFriendEntity) }
    var date by rememberSaveable(giftDetail?.id) { mutableStateOf(giftDetail?.date ?: "") }
    var mood by rememberSaveable(giftDetail?.id) { mutableStateOf(giftDetail?.mood ?: "") }
    var memo by rememberSaveable(giftDetail?.id) { mutableStateOf(giftDetail?.memo ?: "") }

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
                friend = it.friendDetail
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
            text = if (date.isEmpty()) "날짜를 입력해주세요."
                else {
                val (year, month, day) = date.split("-").map { it.toInt() }
                "${year}년 ${StringUtils.format2Digits(month)}월 ${StringUtils.format2Digits(day)}일"
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
            placeHolder = if (giftDetail == null || giftDetail.mood.isEmpty()) "기분을 입력해주세요." else mood,
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
            val gift = GiftDetail(
                category = category,
                friend = friend,
                date = date,
                mood = mood,
                memo = memo,
                giftType = type,
                imgUri = giftDetail?.imgUri ?: ""
            )

            if (giftDetail != null) gift.apply { setId(giftDetail.id) }

            onClickSave(gift)
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
            onChangeText = { onChangeText(it) },
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