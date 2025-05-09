package com.w36495.senty.view.component.imagepicker

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.util.dropShadow
import com.w36495.senty.view.component.SentyAnnotatedCenterAlignedTopAppBar
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyGreen80
import kotlinx.coroutines.launch

@Composable
fun ImagePickerRoute(
    moveToEditGift: (List<Uri>) -> Unit,
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
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        selectedImageUris = selectedImageUri,
        imageUris = imageUris,
        onClickComplete = {
            if (selectedImageUri.isEmpty()) onBackPressed()
            else moveToEditGift(selectedImageUri)
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
    modifier: Modifier = Modifier,
    selectedImageUris: List<Uri>,
    imageUris: List<Uri>,
    onSelectedImage: (Uri) -> Unit,
    onUnSelectedImage: (Int) -> Unit,
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            ImagePickerHeader(
                selectedImageCount = selectedImageUris.size,
                onClickComplete = onClickComplete,
                onBackPressed = onBackPressed,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
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
    selectedImageCount: Int = 0,
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val text = pluralStringResource(id = R.plurals.gift_image_picker_title, count = 1, selectedImageCount)
    val annotatedString = AnnotatedString
        .Builder(text)
        .toAnnotatedString()

    SentyAnnotatedCenterAlignedTopAppBar(
        annotatedString = annotatedString,
        hasBackButton = true,
        actions = {
            Text(
                text = stringResource(id = R.string.common_complete),
                style = SentyTheme.typography.bodyMedium,
                modifier = Modifier
                    .clickable { onClickComplete() }
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
    val coroutineScope = rememberCoroutineScope()

    if (selectedImageUris.isNotEmpty()) {
        val pagerState = rememberPagerState { selectedImageUris.size }

        HorizontalPager(
            modifier = Modifier.aspectRatio(1f),
            state = pagerState,
        ) { page ->
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedImageUris[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
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
    onSelectedImage: (Uri) -> Unit,
    onUnSelectedImage: (Int) -> Unit,
) {
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

                selectedImageUris.size < 3 -> {
                    onSelectedImage(selectedUri)
                }
            }
        }) {
        Image(
            painter = rememberAsyncImagePainter(model = selectedUri),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
        )

        if (selectedImageUris.size == 3) {
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