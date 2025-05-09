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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.w36495.senty.R
import com.w36495.senty.view.component.SentyCenterAlignedTopAppBar
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray10

@Composable
fun ImagePickerRoute(
    moveToEditGift: (Uri) -> Unit,
    onBackPressed: () -> Unit = {},
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    LaunchedEffect(Unit) {
        imageUris = ImagePickerUtils.getAllImages(context)
    }

    ImagePickerScreen(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        selectedImageUri = selectedImageUri,
        imageUris = imageUris,
        onClickComplete = {
            selectedImageUri?.let { uri ->
                moveToEditGift(uri)
            } ?: onBackPressed()
        },
        onSelectedImage = { selectedImageUri = it },
        onBackPressed = onBackPressed,
    )
}

@Composable
private fun ImagePickerScreen(
    modifier: Modifier = Modifier,
    selectedImageUri: Uri?,
    imageUris: List<Uri>,
    onSelectedImage:(Uri) -> Unit,
    onClickComplete: () -> Unit,
    onBackPressed: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFBFBFB))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SentyCenterAlignedTopAppBar(
                title = R.string.common_title_empty,
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

            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                )
            } ?: run {
                Box(modifier = Modifier
                    .aspectRatio(1f)
                    .background(Color(0xFFFBFBFB)))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
            ) {
                items(imageUris) {uri ->
                    Box(modifier = Modifier) {
                        Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .border(1.dp, SentyGray10)
                                .clickable { onSelectedImage(uri) }
                        )

                        if (selectedImageUri == uri) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(SentyBlack.copy(0.1f))) {

                            }
                        }
                    }
                }
            }
        }
    }
}