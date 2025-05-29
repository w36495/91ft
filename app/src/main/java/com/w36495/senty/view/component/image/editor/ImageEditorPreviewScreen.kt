package com.w36495.senty.view.component.image.editor

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Scale
import com.w36495.senty.R
import com.w36495.senty.util.getScreenWidthPx
import com.w36495.senty.view.component.SentyCenterAlignedTopAppBar
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyYellow60

@Composable
fun ImageEditorPreviewRoute(
    vm: ImageEditorPreviewViewModel = hiltViewModel(),
    uris: List<Uri>,
    moveToEditGift: (List<Uri>) -> Unit,
    onBackPressed: () -> Unit,
) {
    val images by vm.editedImages.collectAsState()

    LaunchedEffect(Unit) {
        Log.d("ImageEditorPreviewScreen", images.size.toString())
    }

    ImageEditorPreviewScreen(
        images = uris,
        onClickComplete = { moveToEditGift(uris) },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImageEditorPreviewScreen(
    images: List<Uri>,
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState {
        images.size
    }

    Scaffold(
        topBar = {
            SentyCenterAlignedTopAppBar(
                title = R.string.gift_image_preview_titile,
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFFBFBFB)),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    pageSpacing = 16.dp,
                ) {page ->
                    Box(modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(images[page])
                                    .build()
                            ),
                            contentDescription = "Edited Image",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                }

                BottomSmallPreviewSection(
                    modifier = Modifier,
                    imageUris = images,
                    currentPreviewIndex = pagerState.currentPage,
                )
            }

        }
    }
}

@Composable
fun BottomSmallPreviewSection(
    modifier: Modifier = Modifier,
    imageUris: List<Uri>,
    currentPreviewIndex: Int,
) {
    val context = LocalContext.current
    val screenWidth = getScreenWidthPx()

    Column(
        modifier = modifier.fillMaxWidth()
            .background(Color(0xFFFBFBFB))
    ){
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
                )
            }
        }
    }
}
