package com.w36495.senty.view.component.image.picker.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Scale
import com.vsnappy1.extension.noRippleClickable
import com.w36495.senty.R
import com.w36495.senty.view.component.image.picker.model.GalleryFolderUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryFolderModalBottomSheet(
    galleryFolders: List<GalleryFolderUiModel>,
    bottomSheetState: SheetState,
    onDismiss: () -> Unit,
    onSelected: (GalleryFolderUiModel) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = bottomSheetState,
        containerColor = SentyWhite,
        dragHandle = null,
    ) {
        GalleryFolderModalBottomSheetContents(
            galleryFolders = galleryFolders,
            onClick = onSelected,
            onDismiss = onDismiss,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryFolderModalBottomSheetContents(
    galleryFolders: List<GalleryFolderUiModel>,
    onClick: (GalleryFolderUiModel) -> Unit,
    onDismiss: () -> Unit,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Text(
                text = "사진첩 선택",
                style = SentyTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_close_black_24),
                contentDescription = "gallery folder selection close icon",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
                    .noRippleClickable { onDismiss() },
            )
        }

        HorizontalDivider(
            color = SentyGray20,
            thickness = 0.5.dp,
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(galleryFolders) { folder ->
                GalleryFolderItem(
                    galleryFolder = folder,
                    onClick = { onClick(folder) },
                )
            }
        }
    }

}

@Composable
private fun GalleryFolderItem(
    galleryFolder: GalleryFolderUiModel,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .clickable { onClick() }

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context)
                        .data(galleryFolder.thumbnailUri)
                        .size(600)
                        .scale(Scale.FILL)
                        .build()
                ),
                contentScale = ContentScale.Crop,
                contentDescription = "gallery group thumbnail",
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, SentyGray10),
            )

            Text(
                text = "${galleryFolder.getFolderNameKr()} (${galleryFolder.count})",
                style = SentyTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        HorizontalDivider(
            color = SentyGray20,
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun GalleryFolderModalBottomSheetPreview() {
    SentyTheme {
        GalleryFolderModalBottomSheetContents(
            galleryFolders = List(10) {
                GalleryFolderUiModel(name = "Folder $it", thumbnailUri = Uri.EMPTY, count = it)
            },
            onClick = {},
            onDismiss = {},
        )
    }
}