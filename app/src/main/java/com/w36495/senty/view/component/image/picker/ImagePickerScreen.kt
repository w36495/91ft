package com.w36495.senty.view.component.image.picker

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Scale
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.util.dropShadow
import com.w36495.senty.util.getScreenWidthPx
import com.w36495.senty.view.component.SentyAnnotatedCenterAlignedTopAppBar
import com.w36495.senty.view.component.image.picker.model.ImagePickerContract
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyGreen80
import com.w36495.senty.view.ui.theme.SentyWhite
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

private const val MAX_IMAGE_COUNT = 3
private const val THUMBNAIL_SIZE = 600

@Composable
fun ImagePickerRoute(
    vm: ImagePickerViewModel = hiltViewModel(),
    originalImageCount: Int = 0,
    moveToImagePreview: (List<String>) -> Unit,
    onBackPressed: () -> Unit = {},
) {
    val context = LocalContext.current
    val uiState by vm.state.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState {
        if (uiState.selectedImageUris.isEmpty()) 0
        else uiState.selectedImageUris.size
    }
    val scrollStates = remember { mutableStateMapOf<Int, ScrollState>() }

    LaunchedEffect(Unit) {
        launch {
            vm.effect.collect { effect ->
                when (effect) {
                    ImagePickerContract.Effect.NavigateToBack -> { onBackPressed() }
                    is ImagePickerContract.Effect.NavigateToImagePreview -> {
                        moveToImagePreview(effect.editedImageUris.map { it.toString() })
                    }
                }
            }
        }

        launch {
            snapshotFlow { pagerState.currentPage }
                .distinctUntilChanged()
                .collect { page ->
                    val scrollState = scrollStates.getOrPut(page) { ScrollState(initial = 0) }

                    if (!uiState.initializedPages.contains(page)) {
                        vm.addInitializedPage(page)
                        scrollState.scrollTo(0)
                    }
                }
        }
    }

    LaunchedEffect(uiState.selectedImageUris.size) {
        if (uiState.selectedImageUris.isNotEmpty()) {
            pagerState.animateScrollToPage(uiState.selectedImageUris.lastIndex)
        }
    }

    ImagePickerScreen(
        uiState = uiState,
        totalImageCount = MAX_IMAGE_COUNT.minus(originalImageCount),
        pagerState = pagerState,
        scrollStates = scrollStates,
        onClickNext = {
            val scrollValues = (0 until pagerState.pageCount).map { page ->
                scrollStates[page]?.value ?: 0
            }

            vm.croppedImage(
                context = context,
                scrollValues = scrollValues,
            )
        },
        onSelectedImage = { vm.selectImage(it) },
        onUnSelectedImage = { vm.unselectImage(it) },
        onChangeImageViewportSize = { vm.updateViewportSize(it) },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImagePickerScreen(
    uiState: ImagePickerContract.State,
    totalImageCount: Int,
    pagerState: PagerState,
    scrollStates: SnapshotStateMap<Int, ScrollState>,
    onSelectedImage: (Uri) -> Unit,
    onUnSelectedImage: (Int) -> Unit,
    onClickNext: () -> Unit,
    onChangeImageViewportSize: (IntSize) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            ImagePickerHeader(
                totalImageCount = totalImageCount,
                selectedImageCount = uiState.selectedImageUris.size,
                onClickNext = onClickNext,
                onBackPressed = onBackPressed,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFBFBFB))
        ) {
            Column {
                ImagePickerPreview(
                    selectedImageUris = uiState.selectedImageUris,
                    pagerState = pagerState,
                    scrollStates = scrollStates,
                    onChangeImageViewportSize = onChangeImageViewportSize,
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(2.dp),
                ) {
                    items(uiState.images) {uri ->
                        ImagePickerItem(
                            selectedUri = uri,
                            selectedImageUris = uiState.selectedImageUris,
                            totalImageCount = totalImageCount,
                            onSelectedImage = onSelectedImage,
                            onUnSelectedImage = onUnSelectedImage,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImagePickerHeader(
    totalImageCount: Int,
    selectedImageCount: Int = 0,
    onClickNext: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val text = pluralStringResource(id = R.plurals.gift_image_picker_title, count = 1, selectedImageCount, totalImageCount)
    val annotatedString = AnnotatedString
        .Builder(text)
        .toAnnotatedString()

    SentyAnnotatedCenterAlignedTopAppBar(
        annotatedString = annotatedString,
        hasBackButton = true,
        actions = {
            if (selectedImageCount > 0) {
                Text(
                    text = stringResource(id = R.string.common_next),
                    style = SentyTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable { onClickNext() }
                        .padding(14.dp),
                )
            }
        },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImagePickerPreview(
    modifier: Modifier = Modifier,
    selectedImageUris: List<Uri>,
    pagerState: PagerState,
    scrollStates: SnapshotStateMap<Int, ScrollState>,
    onChangeImageViewportSize: (IntSize) -> Unit,
) {
    val context = LocalContext.current
    val screenWidthPx = getScreenWidthPx()
    val coroutineScope = rememberCoroutineScope()
    
    if (selectedImageUris.isNotEmpty()) {
        HorizontalPager(
            modifier = Modifier.aspectRatio(1f),
            state = pagerState,
        ) { page ->
            val scrollState = scrollStates.getOrPut(page) { ScrollState(0) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { onChangeImageViewportSize(it.size) }
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(selectedImageUris[page])
                            .size(screenWidthPx)
                            .scale(Scale.FILL)
                            .build()
                    ),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                )

                if (page > 0) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_circle_right_24),
                        contentDescription = null,
                        tint = SentyGray60,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .rotate(180f)
                            .padding(8.dp)
                            .dropShadow(
                                shape = CircleShape,
                                offsetX = 0.dp,
                                offsetY = 0.dp,
                                blur = 4.dp,
                            )
                            .noRippleClickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                    )
                }

                if (page < selectedImageUris.lastIndex) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_circle_right_24),
                        contentDescription = null,
                        tint = SentyGray60,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(8.dp)
                            .dropShadow(
                                shape = CircleShape,
                                offsetX = 0.dp,
                                offsetY = 0.dp,
                                blur = 4.dp,
                            )
                            .noRippleClickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                    )
                }

                ImagePickerGuideline()
            }
        }
    } else {
        Box(modifier = modifier
            .aspectRatio(1f)
            .background(Color(0xFFFBFBFB)))
    }
}

@Composable
private fun ImagePickerItem(
    selectedUri: Uri,
    selectedImageUris: List<Uri>,
    totalImageCount: Int,
    onSelectedImage: (Uri) -> Unit,
    onUnSelectedImage: (Int) -> Unit,
) {
    val context = LocalContext.current

    Box(modifier = Modifier
        .aspectRatio(1f)
        .padding(2.dp)
        .border(1.dp, SentyGray10)
        .clickable {
            when {
                selectedImageUris.contains(selectedUri) -> {
                    val index = selectedImageUris.indexOf(selectedUri)
                    onUnSelectedImage(index)
                }

                selectedImageUris.size < totalImageCount -> {
                    onSelectedImage(selectedUri)
                }
            }
        }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(context)
                    .data(selectedUri)
                    .size(THUMBNAIL_SIZE)
                    .scale(Scale.FILL)
                    .build()
            ),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
        )

        if (selectedImageUris.size == totalImageCount) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SentyBlack.copy(0.1f))
            )
        }

        if (selectedImageUris.isNotEmpty()) {
            selectedImageUris.find { it == selectedUri }?.let {
                val index = selectedImageUris.indexOf(it).plus(1)

                Icon(
                    painter = painterResource(
                        id = when (index) {
                            1 -> R.drawable.ic_baseline_counter_1
                            2 -> R.drawable.ic_baseline_counter_2
                            else -> R.drawable.ic_baseline_counter_3
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .border(1.dp, SentyGreen80, CircleShape)
                        .background(SentyWhite, CircleShape),
                    tint = SentyGreen60
                )
            }
        }
    }
}

@Composable
private fun ImagePickerGuideline() {
    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val rowCount = 3
            val columnCount = 3

            // 수직선
            for (i in 1 until columnCount) {
                val x = canvasWidth * i / columnCount
                drawLine(
                    color = Color.White,
                    start = Offset(x, 0f),
                    end = Offset(x, canvasHeight),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // 수평선
            for (i in 1 until rowCount) {
                val y = canvasHeight * i / rowCount
                drawLine(
                    color = Color.White,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
    }
}