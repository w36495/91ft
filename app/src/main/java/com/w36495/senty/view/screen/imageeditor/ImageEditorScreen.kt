package com.w36495.senty.view.screen.imageeditor

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.view.component.SentyAsyncImage
import com.w36495.senty.view.component.SentyCenterAlignedTopAppBar
import com.w36495.senty.view.component.image.editor.ImageCropper
import com.w36495.senty.view.component.image.editor.model.ImageEditToolType
import com.w36495.senty.view.screen.imageeditor.model.ImageEditorContact
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun ImageEditorRoute(
    vm: ImageEditorViewModel = hiltViewModel(),
    imageUris: List<Uri>,
    moveToEditPreview: (List<String>) -> Unit,
    onBackPressed: () -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    LaunchedEffect(Unit) {
        vm.setImageUris(imageUris)
    }

    val context = LocalContext.current
    val uiState by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is ImageEditorContact.Effect.ShowSnackBar -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is ImageEditorContact.Effect.ShowError -> { onShowGlobalErrorSnackBar(effect.throwable) }
                is ImageEditorContact.Effect.NavigateToImageEditorPreview -> {
                    moveToEditPreview(effect.imageFileUris)
                }
                ImageEditorContact.Effect.NavigateToBack -> { onBackPressed() }
                else -> {}
            }
        }
    }

    ImageEditorScreen(
        uiState = uiState,
        onClickComplete = { vm.handleEvent(ImageEditorContact.Event.OnClickComplete(context)) },
        onSaveEdit = { vm.handleEvent(ImageEditorContact.Event.UpdateEditImage(it)) },
        onClickSave = { vm.handleEvent(ImageEditorContact.Event.OnClickSave) },
        onChangeImageSpec = { offset, size ->
            vm.handleEvent(ImageEditorContact.Event.UpdateEditImageSpec(offset, size))
        },
        onClickPrev = { vm.handleEvent(ImageEditorContact.Event.OnClickPrev) },
        onClickNext = { vm.handleEvent(ImageEditorContact.Event.OnClickNext) },
        onDismissSaveDialog = { vm.handleEvent(ImageEditorContact.Event.OnDismissSaveDialog) },
        onClickImageEditTool = { type ->
            when (type) {
                ImageEditToolType.FLIP_VERTICAL -> vm.handleEvent(ImageEditorContact.Event.OnClickFlipVertical)
                ImageEditToolType.FLIP_HORIZONTAL -> vm.handleEvent(ImageEditorContact.Event.OnClickFlipHorizontal)
            }
        },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImageEditorScreen(
    uiState: ImageEditorContact.State,
    onSaveEdit: (Bitmap) -> Unit,
    onClickPrev: () -> Unit,
    onClickNext: () -> Unit,
    onClickSave: () -> Unit,
    onClickComplete: () -> Unit,
    onDismissSaveDialog: () -> Unit,
    onChangeImageSpec: (Offset, IntSize) -> Unit,
    onClickImageEditTool: (ImageEditToolType) -> Unit,
    onBackPressed: () -> Unit,
) {
    val isReadyToCrop by remember(uiState.editImageState) {
        derivedStateOf { uiState.editImageState.size != IntSize.Zero}
    }
    Scaffold(
        topBar = {
            ImageEditorHeader(
                isFirst = uiState.currentEditIndex == 0,
                hasNext = uiState.currentEditIndex != uiState.originalImageUris.lastIndex,
                onClickPrev = onClickPrev,
                onClickNext = onClickNext,
                onClickComplete = onClickComplete,
                onBackPressed = onBackPressed,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFBFBFB)),
        ) {
            if (uiState.originalImageUris.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFFBFBFB))
                )
            } else {
                SentyAsyncImage(
                    model = uiState.originalImageUris[uiState.currentEditIndex],
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .onGloballyPositioned {
                            onChangeImageSpec(it.positionInParent(), it.size)
                        }
                        .graphicsLayer {
                            scaleX = if (uiState.isFlippedHorizontal) -1f else 1f
                            scaleY = if (uiState.isFlippedVertical) -1f else 1f
                        },
                )
            }

            if (isReadyToCrop) {
                CropSection(
                    imageOffset = uiState.editImageState.offset,
                    imageSize = uiState.editImageState.size,
                    imageUri = if (uiState.originalImageUris.isEmpty()) null else uiState.originalImageUris[uiState.currentEditIndex],
                    isEditing = uiState.isEditing,
                    isFlippedVertical = uiState.isFlippedVertical,
                    isFlippedHorizontal = uiState.isFlippedHorizontal,
                    onCompleteCrop = onSaveEdit,
                )
            }

            EditToolBox(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                onClickTool = onClickImageEditTool,
                onClickSave = onClickSave,
            )
        }
    }

    if (uiState.showSaveDialog) {
        BasicAlertDialog(
            title = stringResource(id = R.string.gift_image_editor_save_dialog_title),
            hasCancel = true,
            onDismiss = onDismissSaveDialog,
            onComplete = onClickSave
        )
    }
}

@Composable
private fun ImageEditorHeader(
    isFirst: Boolean,
    hasNext: Boolean,
    onClickPrev: () -> Unit,
    onClickNext: () -> Unit,
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    SentyCenterAlignedTopAppBar(
        title = R.string.gift_image_editor_title,
        hasBackButton = true,
        onBackPressed = onBackPressed,
        actions = {
            if (!isFirst) {
                Text(
                    text = stringResource(id = R.string.common_prev),
                style = SentyTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { onClickPrev() }
                    .padding(14.dp),
                )
            }

            Text(
                text = stringResource(id = if (hasNext) R.string.common_next else R.string.common_complete),
                style = SentyTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { if (hasNext) onClickNext() else onClickComplete() }
                    .padding(14.dp),
            )
        }
    )
}

@Composable
private fun EditToolBox(
    modifier: Modifier = Modifier,
    onClickSave: () -> Unit,
    onClickTool: (ImageEditToolType) -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(SentyBlack.copy(0.6f), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 36.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_round_flip_24),
            contentDescription = "image editor crop icon",
            tint = SentyWhite,
            modifier = Modifier
                .noRippleClickable { onClickTool(ImageEditToolType.FLIP_HORIZONTAL) },
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_round_flip_24),
            contentDescription = "image editor crop icon",
            tint = SentyWhite,
            modifier = Modifier
                .rotate(90f)
                .noRippleClickable { onClickTool(ImageEditToolType.FLIP_VERTICAL) },
        )

        Row {
            Text(
                text = stringResource(id = R.string.common_reset),
                style = SentyTheme.typography.bodySmall
                    .copy(color = SentyWhite),
                modifier = Modifier
                    .noRippleClickable { },
            )

            Text(
                text = stringResource(id = R.string.common_save),
                style = SentyTheme.typography.bodySmall
                    .copy(color = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .padding(start = 24.dp)
                    .noRippleClickable { onClickSave() },
            )
        }

    }
}

@Composable
private fun CropSection(
    isEditing: Boolean = false,
    imageOffset: Offset,
    imageSize: IntSize,
    imageUri: Uri? = null,
    isFlippedVertical: Boolean,
    isFlippedHorizontal: Boolean,
    onCompleteCrop: (Bitmap) -> Unit,
) {
    val imageRect by remember {
        mutableStateOf(
            Rect(
                imageOffset.x,
                imageOffset.y,
                imageOffset.x + imageSize.width,
                imageOffset.y + imageSize.height,
            )
        )
    }

    imageUri?.let { uri ->
        ImageCropper(
            imageUri = uri,
            imageOffset = imageOffset,
            imageSize = imageSize,
            imageRect = imageRect,
            isEditing = isEditing,
            isFlippedVertical = isFlippedVertical,
            isFlippedHorizontal = isFlippedHorizontal,
            onCompleteCrop = onCompleteCrop
        )
    }
}



@Preview(showBackground = true)
@Composable
private fun ImageEditorScreenPreview() {
    SentyTheme {
        ImageEditorScreen(
            uiState = ImageEditorContact.State(),
            onClickComplete = {},
            onSaveEdit = {},
            onChangeImageSpec = { _, _ -> },
            onClickSave = {},
            onClickNext = {},
            onDismissSaveDialog = {},
            onClickImageEditTool = {},
            onClickPrev = {},
            onBackPressed = {},
        )
    }
}