package com.w36495.senty.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.size.Scale
import com.w36495.senty.util.getScreenWidthPx
import com.w36495.senty.view.ui.theme.SentyGray10

@Composable
fun SentyAsyncImage(
    modifier: Modifier = Modifier,
    model: Any,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderColor: Color = SentyGray10,
) {
    val context = LocalContext.current
    val screenWidthPx = getScreenWidthPx()

    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(model)
            .size(screenWidthPx)
            .scale(Scale.FILL)
            .build(),
        contentDescription = contentDescription,
        modifier = modifier,
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(placeholderColor),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        },
        contentScale = contentScale,
    )
}
