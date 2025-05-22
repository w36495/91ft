package com.w36495.senty.view.component.image.picker

import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Scale
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.util.ImageConverter
import com.w36495.senty.util.dropShadow
import com.w36495.senty.util.getScreenWidthPx
import com.w36495.senty.view.component.SentyAnnotatedCenterAlignedTopAppBar
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyGreen80
import kotlinx.coroutines.launch

private const val MAX_IMAGE_COUNT = 3

@Composable
fun ImagePickerRoute(
    originalImageCount: Int = 0,
    moveToImageEditor: (List<Uri>) -> Unit,
    onBackPressed: () -> Unit = {},
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    LaunchedEffect(Unit) {
        imageUris = ImagePickerUtils.getAllImages(context)
        selectedImageUri = selectedImageUri + imageUris.first()
    }

    ImagePickerScreen(
        selectedImageUris = selectedImageUri,
        imageUris = imageUris,
        totalImageCount = MAX_IMAGE_COUNT.minus(originalImageCount),
        onClickNext = {
            if (selectedImageUri.isEmpty()) onBackPressed()
            else moveToImageEditor(selectedImageUri)
        },
        onSelectedImage = {
            selectedImageUri = selectedImageUri + listOf(it)
        },
        onUnSelectedImage = {
            selectedImageUri = selectedImageUri.filterIndexed { index, _ -> index != it }
        },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImagePickerScreen(
    selectedImageUris: List<Uri>,
    imageUris: List<Uri>,
    totalImageCount: Int,
    onSelectedImage: (Uri) -> Unit,
    onUnSelectedImage: (Int) -> Unit,
    onClickNext: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            ImagePickerHeader(
                totalImageCount = totalImageCount,
                selectedImageCount = selectedImageUris.size,
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
                    selectedImageUris = selectedImageUris,
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(2.dp),
                ) {
                    items(imageUris) {uri ->
                        ImagePickerItem(
                            selectedUri = uri,
                            selectedImageUris = selectedImageUris,
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
            Text(
                text = stringResource(id = R.string.common_next),
                style = SentyTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { onClickNext() }
                    .padding(14.dp),
            )
        },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImagePickerPreview(
    modifier: Modifier = Modifier,
    selectedImageUris: List<Uri>,
) {
    val context = LocalContext.current
    val screenWidthPx = getScreenWidthPx()
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState {
        if (selectedImageUris.isEmpty()) 0 else selectedImageUris.size
    }
    val scrollStates = remember { mutableStateMapOf<Int, ScrollState>() }
    val initializedPages = remember { mutableSetOf<Int>() }

    LaunchedEffect(pagerState.currentPage) {
        val page = pagerState.currentPage
        val scrollState = scrollStates.getOrPut(page) { ScrollState(initial = 0) }

        if (!initializedPages.contains(page)) {
            initializedPages.add(page)
            scrollState.scrollTo(0)
        }
    }

    if (selectedImageUris.isNotEmpty()) {
        HorizontalPager(
            modifier = Modifier.aspectRatio(1f),
            state = pagerState,
        ) { page ->
            val scrollState = scrollStates.getOrPut(page) { ScrollState(0) }

            Box(modifier = Modifier.fillMaxSize()) {
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
    val screenWidthPx = getScreenWidthPx()

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
                    .size(screenWidthPx)
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
                        .border(1.dp, SentyGreen80, CircleShape),
                    tint = SentyGreen60
                )
            }
        }
    }
}