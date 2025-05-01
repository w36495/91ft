package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.R
import com.w36495.senty.view.screen.gift.edit.model.ImageSelectionType
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyGray40
import com.w36495.senty.view.ui.theme.SentyGray80

@Composable
fun ImageSelectionDialog(
    onSelect: (ImageSelectionType) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clickable { onSelect(ImageSelectionType.CAMERA) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = SentyGray80,
                        )

                        Text(
                            text = stringResource(id = R.string.gift_edit_image_selection_type_camera),
                            textAlign = TextAlign.Center,
                            style = SentyTheme.typography.titleMedium
                                .copy(color = SentyBlack),
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                Divider(
                    modifier = Modifier
                        .width(0.5.dp)
                        .fillMaxHeight(),
                    color = SentyGray40,
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .clickable { onSelect(ImageSelectionType.GALLERY) },
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = SentyGray80,
                        )

                        Text(
                            text = stringResource(id = R.string.gift_edit_image_selection_type_gallery),
                            textAlign = TextAlign.Center,
                            style = SentyTheme.typography.titleMedium
                                .copy(color = SentyBlack),
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ImageSelectionDialogPreview() {
    SentyTheme {
        ImageSelectionDialog(
            onDismiss = {},
            onSelect = {},
        )
    }
}