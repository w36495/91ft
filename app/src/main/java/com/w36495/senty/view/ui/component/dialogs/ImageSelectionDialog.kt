package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.view.screen.ui.theme.SentyTheme

@Composable
fun ImageSelectionDialog(
    hasImagePath: Boolean,
    onDismiss: () -> Unit,
    onClickCamera: () -> Unit,
    onClickGallery: () -> Unit,
    onClickDelete: () -> Unit,
) {
    ImageSelectionDialogContents(
        hasImagePath = hasImagePath,
        onDismiss = { onDismiss() },
        onClickCamera = {
            onClickCamera()
            onDismiss()
        },
        onClickGallery = {
            onClickGallery()
            onDismiss()
        },
        onClickDelete = onClickDelete
    )
}

@Composable
private fun ImageSelectionDialogContents(
    modifier: Modifier = Modifier,
    hasImagePath: Boolean,
    onDismiss: () -> Unit,
    onClickCamera: () -> Unit,
    onClickGallery: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable { onClickCamera() }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(onClick = { onClickCamera() }) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
                            }
                            Text(
                                text = "카메라를 통해 \n이미지 가져오기",
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable { onClickGallery() }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            IconButton(onClick = { onClickGallery() }) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null)
                            }
                            Text(
                                text = "갤러리를 통해\n이미지 가져오기",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (hasImagePath) {
                HorizontalDivider()
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 24.dp)
                        .clickable { onClickDelete() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "이미지 삭제하기",
                        color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageSelectionDialogPreview() {
    SentyTheme {
        ImageSelectionDialogContents(
            hasImagePath = true,
            onDismiss = {},
            onClickCamera = {},
            onClickGallery = {},
            onClickDelete = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageSelectionDialogPreview2() {
    SentyTheme {
        ImageSelectionDialogContents(
            hasImagePath = false,
            onDismiss = {},
            onClickCamera = {},
            onClickGallery = {},
            onClickDelete = {},
        )
    }
}