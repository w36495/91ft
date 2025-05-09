package com.w36495.senty.view.component.image.editor

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Scale
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.util.getScreenWidthPx
import com.w36495.senty.view.component.SentyCenterAlignedTopAppBar
import com.w36495.senty.view.component.image.editor.model.ImageEditToolType
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyWhite
import com.w36495.senty.view.ui.theme.SentyYellow60

@Composable
fun ImageEditorRoute(
    imageUris: List<Uri>,
    moveToEditGift: (List<Uri>) -> Unit,
    onBackPressed: () -> Unit,
) {
    var editedImageUris by remember { mutableStateOf(emptyList<Uri>()) }

    ImageEditorScreen(
        imageUris = imageUris,
        onClickComplete = { moveToEditGift(editedImageUris) },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImageEditorScreen(
    imageUris: List<Uri>,
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    var currentPreviewIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val screenWidth = getScreenWidthPx()

    Scaffold(
        topBar = {
            ImageEditorHeader(
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
            Column {
                Box(
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(context)
                                .data(imageUris[currentPreviewIndex])
                                .size(screenWidth)
                                .scale(Scale.FILL)
                                .build()
                        ),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center),
                    )

                    EditToolBox(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                    )
                }

                BottomSmallPreviewSection(
                    imageUris = imageUris,
                    currentPreviewIndex = currentPreviewIndex,
                    onChangeCurrentPreviewIndex = { currentPreviewIndex = it },
                )
            }
        }
    }
}

@Composable
private fun ImageEditorHeader(
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    SentyCenterAlignedTopAppBar(
        title = R.string.gift_image_editor_title,
        hasBackButton = true,
        onBackPressed = onBackPressed,
        actions = {
            Text(
                text = stringResource(id = R.string.common_complete),
                style = SentyTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { onClickComplete() }
                    .padding(14.dp),
            )
        }
    )
}

@Composable
private fun EditToolBox(
    modifier: Modifier = Modifier,
    onClickTool: (ImageEditToolType) -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(SentyBlack.copy(0.6f), RoundedCornerShape(14.dp))
            .padding(vertical = 8.dp, horizontal = 36.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_round_crop_24),
            contentDescription = "image editor crop icon",
            tint = SentyWhite,
            modifier = Modifier
                .noRippleClickable { onClickTool(ImageEditToolType.CROP) },
        )

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
    }
}

@Composable
fun CropBoxOverlay(
    cropRect: Rect,
    onCropRectChanged: (Rect) -> Unit
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(cropRect.left.toInt(), cropRect.top.toInt()) }
            .size(cropRect.width.dp) // 정사각형이므로 width = height
            .border(2.dp, Color.White)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newLeft = cropRect.left + dragAmount.x
                    val newTop = cropRect.top + dragAmount.y
                    onCropRectChanged(Rect(newLeft, newTop, newLeft + cropRect.width, newTop + cropRect.width))
                }
            }
    )
}

@Composable
private fun BottomSmallPreviewSection(
    imageUris: List<Uri>,
    currentPreviewIndex: Int,
    onChangeCurrentPreviewIndex: (Int) -> Unit,
) {
    val context = LocalContext.current
    val screenWidth = getScreenWidthPx()

    HorizontalDivider(
        color = SentyGray20,
        thickness = 0.5.dp,
        modifier = Modifier.fillMaxWidth(),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(imageUris.size) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(imageUris[it])
                        .size(screenWidth)
                        .scale(Scale.FILL)
                        .build()
                ),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(60.dp)
                    .border(
                        width = 2.dp,
                        color = if (currentPreviewIndex == it) SentyYellow60 else SentyGray20,
                    )
                    .clickable { onChangeCurrentPreviewIndex(it) },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageEditorScreenPreview() {
    SentyTheme {
        ImageEditorScreen(
            imageUris = emptyList(),
            onClickComplete = {},
            onBackPressed = {},
        )
    }
}