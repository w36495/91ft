package com.w36495.senty.view.screen.gift.edit

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.data.domain.GiftType
import com.w36495.senty.util.StringUtils
import com.w36495.senty.util.checkCameraPermission
import com.w36495.senty.util.getUriFile
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.component.SentyAsyncImage
import com.w36495.senty.view.screen.friend.FriendSelectionDialog
import com.w36495.senty.view.screen.friend.model.FriendUiModel
import com.w36495.senty.view.screen.gift.category.GiftCategorySelectionDialog
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.gift.edit.contact.EditGiftContact
import com.w36495.senty.view.screen.gift.edit.model.EditGiftUiModel
import com.w36495.senty.view.screen.gift.edit.model.EditImage
import com.w36495.senty.view.screen.gift.edit.model.ImageSelectionType
import com.w36495.senty.view.screen.gift.edit.model.getImageData
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.dialogs.BasicCalendarDialog
import com.w36495.senty.view.ui.component.dialogs.ImageSelectionDialog
import com.w36495.senty.view.ui.component.textFields.SentyMultipleTextField
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGray80
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun EditGiftRoute(
    vm: EditGiftViewModel = hiltViewModel(),
    padding: PaddingValues,
    giftId: String? = null,
    moveToGiftCategories: () -> Unit,
    moveToFriendAdd: () -> Unit,
    moveToHome: () -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(giftId) {
        giftId?.let { vm.getGift(it) }
    }

    val uiState by vm.state.collectAsStateWithLifecycle()
    var tempImageUri by remember { mutableStateOf(Uri.EMPTY) }

    val takePhotoFromCamera =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) { vm.handleEvent(EditGiftContact.Event.UpdateImage(tempImageUri)) }
        }

    val launcherCameraPermission = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            tempImageUri = context.getUriFile()
            takePhotoFromCamera.launch(tempImageUri)
        } else {
            vm.sendEffect(EditGiftContact.Effect.ShowToast("카메라 권한이 거부되어있습니다."))
        }
    }

    val takePhotoFromGallery =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri -> vm.handleEvent(EditGiftContact.Event.UpdateImage(uri)) }
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

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is EditGiftContact.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    vm.sendEffect(EditGiftContact.Effect.NavigateToBack)
                }
                is EditGiftContact.Effect.ShowError -> {
                    onShowGlobalErrorSnackBar(effect.throwable)
                }
                EditGiftContact.Effect.ShowCamera -> {
                    val hasPermission = context.checkCameraPermission(arrayOf(Manifest.permission.CAMERA))

                    if (!hasPermission) {
                        launcherCameraPermission.launch(Manifest.permission.CAMERA)
                    } else {
                        tempImageUri = context.getUriFile()
                        takePhotoFromCamera.launch(tempImageUri)
                    }
                }
                EditGiftContact.Effect.ShowGallery -> {
                    takePhotoFromGallery.launch(Intent.createChooser(takePhotoFromGalleryIntent, "Select Picture"))
                }
                EditGiftContact.Effect.NavigateToBack -> {
                    moveToHome()
                }
                EditGiftContact.Effect.NavigateToFriendAdd -> {
                    moveToFriendAdd()
                }
                EditGiftContact.Effect.NavigateToGiftCategories -> {
                    moveToGiftCategories()
                }
            }
        }
    }

    EditGiftScreen(
        modifier = Modifier.navigationBarsPadding(),
        uiState = uiState,
        isEditMode = giftId?.let { true } ?: false,
        onRemoveImageClick = { vm.handleEvent(EditGiftContact.Event.RemoveImage(it)) },
        onPressedBack = { vm.handleEvent(EditGiftContact.Event.OnClickBack) },
        onClickSave = { 
            giftId?.let {
                vm.handleEvent(EditGiftContact.Event.OnClickEdit)
            } ?: vm.handleEvent(EditGiftContact.Event.OnClickSave)
        },
        onClickImageAdd = { vm.handleEvent(EditGiftContact.Event.OnClickImageAdd) },
        onClickDate = { vm.handleEvent(EditGiftContact.Event.OnClickDate) },
        onClickFriend = { vm.handleEvent(EditGiftContact.Event.OnClickFriend) },
        onClickFriendAdd = { vm.handleEvent(EditGiftContact.Event.OnClickFriendAdd) },
        onClickGiftCategory = { vm.handleEvent(EditGiftContact.Event.OnClickGiftCategory) },
        onClickGiftCategoriesEdit = { vm.handleEvent(EditGiftContact.Event.OnClickGiftCategoriesEdit) },
        onChangeMood = { vm.handleEvent(EditGiftContact.Event.UpdateMood(it)) },
        onChangeMemo = { vm.handleEvent(EditGiftContact.Event.UpdateMemo(it)) },
        onSelectGiftType = { vm.handleEvent(EditGiftContact.Event.OnSelectGiftType(it)) },
        onSelectDate = { vm.handleEvent(EditGiftContact.Event.OnSelectDate(it)) },
        onSelectFriend = { vm.handleEvent(EditGiftContact.Event.OnSelectFriend(it)) },
        onSelectGiftCategory = { vm.handleEvent(EditGiftContact.Event.OnSelectGiftCategory(it)) },
        onSelectImageSelectionType = { vm.handleEvent(EditGiftContact.Event.OnSelectImageSelectionType(it)) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGiftScreen(
    modifier: Modifier = Modifier,
    uiState: EditGiftContact.State,
    isEditMode: Boolean,
    onRemoveImageClick: (String) -> Unit,
    onPressedBack: () -> Unit,
    onClickSave: () -> Unit,
    onClickImageAdd: () -> Unit,
    onClickDate: () -> Unit,
    onClickFriend: () -> Unit,
    onClickFriendAdd: () -> Unit,
    onClickGiftCategory: () -> Unit,
    onClickGiftCategoriesEdit: () -> Unit,
    onChangeMood: (String) -> Unit,
    onChangeMemo: (String) -> Unit,
    onSelectGiftType: (GiftType) -> Unit,
    onSelectFriend: (FriendUiModel?) -> Unit,
    onSelectDate: (String?) -> Unit,
    onSelectGiftCategory: (GiftCategoryUiModel?) -> Unit,
    onSelectImageSelectionType: (ImageSelectionType?) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = if (isEditMode) R.string.gift_edit_title_edit else R.string.gift_edit_title_save),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onPressedBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SentyWhite)
                .noRippleClickable(
                    onClick = { focusManager.clearFocus() },
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ImgSection(
                    modifier = Modifier.fillMaxWidth(),
                    giftImages = uiState.gift.images.toList(),
                    onRemoveImageClick = onRemoveImageClick,
                    onAddImageClick = { onClickImageAdd() }
                )
                InputSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    gift = uiState.gift,
                    isEditMode = isEditMode,
                    isErrorGiftCategory = uiState.isErrorGiftCategory,
                    isErrorFriend = uiState.isErrorFriend,
                    isErrorDate = uiState.isErrorDate,
                    onClickSave = onClickSave,
                    onClickDate = onClickDate,
                    onClickFriend = onClickFriend,
                    onClickGiftCategory = onClickGiftCategory,
                    onChangeMood = onChangeMood,
                    onChangeMemo = onChangeMemo,
                    onSelectGiftType = onSelectGiftType,
                    onSelectDate = onSelectDate,
                )
            }

            when {
                uiState.isLoading -> {
                    LoadingCircleIndicator(
                        hasBackGround = false,
                    )
                }
                uiState.showFriendsDialog -> {
                    FriendSelectionDialog(
                        onDismiss = { onSelectFriend(null) },
                        onClickFriend = { onSelectFriend(it) },
                        onClickFriendAdd = { onClickFriendAdd() },
                    )
                }
                uiState.showGiftCategoriesDialog -> {
                    GiftCategorySelectionDialog(
                        onClickCategory = { onSelectGiftCategory(it) },
                        onClickCategoriesEdit = onClickGiftCategoriesEdit,
                        onDismiss = { onSelectGiftCategory(null) },
                    )
                }
                uiState.showImageSelectionDialog -> {
                    ImageSelectionDialog(
                        onDismiss = { onSelectImageSelectionType(null) },
                        onSelect = onSelectImageSelectionType,
                    )
                }
                uiState.showDatePickerDialog -> {
                    BasicCalendarDialog(
                        onDismiss = { onSelectDate(null) },
                        onSelectedDate = { year, month, day ->
                            val date = "$year-${StringUtils.format2Digits(month + 1)}-${StringUtils.format2Digits(day)}"
                            onSelectDate(date)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImgSection(
    modifier: Modifier = Modifier,
    giftImages: List<Pair<String, EditImage>>,
    onRemoveImageClick: (String) -> Unit,
    onAddImageClick: () -> Unit,
) {
    LazyRow(
        modifier = modifier
            .aspectRatio(1f),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (giftImages.isNotEmpty()) {
            itemsIndexed(giftImages) { index, image ->
                val (imageName, imagePath) = image

                DisplayGiftImage(
                    modifier = Modifier.fillMaxWidth(),
                    giftImage = imagePath,
                    onRemoveImageClick = { onRemoveImageClick(imageName) }
                )

                if (index < giftImages.lastIndex) {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (index == giftImages.lastIndex && index < 2) {
                    AddGiftImage(
                        currentIndex = index + 1,
                        onAddImageClick = onAddImageClick
                    )
                }
            }
        } else {
            items(1) {
                AddGiftImage(
                    currentIndex = 0,
                    onAddImageClick = onAddImageClick
                )
            }
        }
    }
}

@Composable
private fun DisplayGiftImage(
    modifier: Modifier = Modifier,
    giftImage: EditImage,
    onRemoveImageClick: () -> Unit,
) {
    val imageData = giftImage.getImageData()

    Box(
        modifier = modifier.aspectRatio(1f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(8.dp)
        ) {
            SentyAsyncImage(
                model = imageData,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .border(
                        width = 0.5.dp,
                        color = SentyGray20,
                        shape = RoundedCornerShape(12.dp)
                    ),
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(24.dp)
                .background(SentyWhite, CircleShape))
            Icon(
                imageVector = Icons.Rounded.Cancel,
                contentDescription = "Gift Image Remove",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable { onRemoveImageClick() },
                tint = SentyBlack,
            )
        }

    }
}

@Composable
private fun AddGiftImage(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    onAddImageClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(8.dp)
            .drawBehind {
                drawRoundRect(
                    color = SentyGray60,
                    style = Stroke(
                        width = 8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    ),
                    cornerRadius = CornerRadius(25f, 25f)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable { onAddImageClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                tint = SentyGray80,
            )

            Text(
                text = stringResource(id = R.string.gift_edit_image_text),
                textAlign = TextAlign.Center,
                style = SentyTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = "(${currentIndex}/3)",
                textAlign = TextAlign.Center,
                style = SentyTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun InputSection(
    modifier: Modifier = Modifier,
    gift: EditGiftUiModel,
    isEditMode: Boolean,
    isErrorGiftCategory: Boolean,
    isErrorFriend: Boolean,
    isErrorDate: Boolean,
    onClickSave: () -> Unit,
    onClickDate: () -> Unit,
    onClickFriend: () -> Unit,
    onClickGiftCategory: () -> Unit,
    onChangeMood: (String) -> Unit,
    onChangeMemo: (String) -> Unit,
    onSelectGiftType: (GiftType) -> Unit,
    onSelectDate: (String?) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier.padding(top = 16.dp)) {
        GiftTypeSection(
            modifier = Modifier.fillMaxWidth(),
            type = gift.type,
            onChangeToReceived = {
                focusManager.clearFocus()
                onSelectGiftType(GiftType.RECEIVED)
            },
            onChangeToSent = {
                focusManager.clearFocus()
                onSelectGiftType(GiftType.SENT)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.gift_edit_category_text,
            enable = false,
            text = gift.categoryName,
            isRequired = true,
            isError = isErrorGiftCategory,
            errorMsg = R.string.gift_edit_category_error_text,
            placeHolder = gift.categoryName.ifEmpty { stringResource(id = R.string.gift_edit_category_hint_text) },
            onChangeText = { },
            onClick = {
                focusManager.clearFocus()
                onClickGiftCategory()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.gift_edit_friend_text,
            text = gift.friendName,
            placeHolder = gift.friendName.ifEmpty { stringResource(id = R.string.gift_edit_friend_hint_text) },
            onChangeText = { },
            enable = false,
            isRequired = true,
            isError = isErrorFriend,
            errorMsg = R.string.gift_edit_friend_error_text,
            onClick = {
                focusManager.clearFocus()
                onClickFriend()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.gift_edit_date_text,
            text = if (gift.date.isNotEmpty()) {
                val (year, month, day) = gift.date.split("-").map { it.toInt() }

                "${year}년 ${StringUtils.format2Digits(month)}월 ${StringUtils.format2Digits(day)}일"
            } else gift.date,
            isError = isErrorDate,
            errorMsg = R.string.gift_edit_date_error_text,
            enable = false,
            placeHolder = gift.date.ifEmpty { stringResource(id = R.string.gift_edit_date_hint_text) },
            onChangeText = { onSelectDate(it) },
            isRequired = true,
            onClick = {
                focusManager.clearFocus()
                onClickDate()
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextSection(
            modifier = Modifier.fillMaxWidth(),
            title = R.string.gift_edit_mood_text,
            text = gift.mood,
            placeHolder = gift.mood.ifEmpty { stringResource(id = R.string.gift_edit_mood_hint_text) },
            onChangeText = onChangeMood,
            onClick = {}
        )

        Text(
            text = stringResource(id = R.string.gift_edit_memo_text),
            style = SentyTheme.typography.labelSmall,
            modifier = Modifier
                .padding(bottom = 12.dp, top = 16.dp)
                .noRippleClickable { focusManager.clearFocus() },
        )

        SentyMultipleTextField(
            text = gift.memo,
            onChangeText = onChangeMemo,
            textStyle = SentyTheme.typography.bodyMedium,
        )

        SentyFilledButton(
            text = stringResource(id = if (isEditMode) { R.string.common_edit } else R.string.common_save),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp, top = 32.dp),
            onClick = {
                focusManager.clearFocus()
                onClickSave()
            },
        )
    }
}

@Composable
private fun TextSection(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    text: String,
    placeHolder: String,
    isRequired: Boolean = false,
    enable: Boolean = true,
    isError: Boolean = false,
    @StringRes errorMsg: Int? = null,
    onChangeText: (String) -> Unit,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = title),
                style = SentyTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (isRequired) {
                Text(
                    text = stringResource(id = R.string.common_required_star),
                    style = SentyTheme.typography.labelMedium
                        .copy(MaterialTheme.colorScheme.error),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        SentyTextField(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            hint = placeHolder,
            isError = isError,
            errorMsg = errorMsg?.let { stringResource(id = it) } ?: "",
            onChangeText = { onChangeText(it) },
            enabled = enable,
            textStyle = SentyTheme.typography.bodyMedium,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GiftTypeSection(
    modifier: Modifier = Modifier,
    type: GiftType,
    onChangeToReceived: () -> Unit,
    onChangeToSent: () -> Unit,
) {
    Row(modifier = modifier) {
        Chip(
            modifier = Modifier.weight(1f),
            colors = ChipDefaults.chipColors(
                backgroundColor = if (type == GiftType.RECEIVED) SentyGreen60 else Color.White
            ),
            border = BorderStroke(1.dp, SentyGreen60),
            shape = RoundedCornerShape(10.dp),
            onClick = { onChangeToReceived() },
        ) {
            Text(
                text = stringResource(id = R.string.common_received_gift_text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = if (type == GiftType.RECEIVED) Color.White else Color.Black,
                textAlign = TextAlign.Center,
                style = if (type == GiftType.RECEIVED) SentyTheme.typography.titleMedium else SentyTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Chip(
            modifier = Modifier.weight(1f),
            colors = ChipDefaults.chipColors(
                backgroundColor = if (type == GiftType.SENT) SentyGreen60 else Color.White
            ),
            border = BorderStroke(1.dp, SentyGreen60),
            shape = RoundedCornerShape(10.dp),
            onClick = { onChangeToSent() },
        ) {
            Text(
                text = stringResource(id = R.string.common_sent_gift_text),
                color = if (type == GiftType.SENT) Color.White else Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = if (type == GiftType.SENT) SentyTheme.typography.titleMedium else SentyTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 1500)
@Composable
private fun GiftAddContentsPreview() {
    SentyTheme {
        EditGiftScreen(
            uiState = EditGiftContact.State(),
            isEditMode = false,
            onRemoveImageClick = {},
            onPressedBack = { /*TODO*/ },
            onClickSave = { /*TODO*/ },
            onClickImageAdd = { /*TODO*/ },
            onClickDate = { /*TODO*/ },
            onClickFriend = { /*TODO*/ },
            onClickFriendAdd = { /*TODO*/ },
            onClickGiftCategory = { /*TODO*/ },
            onClickGiftCategoriesEdit = { /*TODO*/ },
            onChangeMood = {},
            onChangeMemo = {},
            onSelectGiftType = {},
            onSelectFriend = {},
            onSelectDate = {},
            onSelectGiftCategory ={}
        ) {

        }
    }
}