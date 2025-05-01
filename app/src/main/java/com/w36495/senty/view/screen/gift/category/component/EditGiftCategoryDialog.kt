package com.w36495.senty.view.screen.gift.category.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.util.dropShadow
import com.w36495.senty.view.screen.gift.category.contact.GiftCategoryContact
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.view.ui.theme.SentyBlack
import com.w36495.senty.view.ui.theme.SentyWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGiftCategoryDialog(
    isLoading: Boolean,
    giftCategory: GiftCategoryUiModel? = null,
    onComplete: (GiftCategoryUiModel) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var categoryName by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    var dialogHeight by remember { mutableStateOf(0) }
    val dialogHeightDp = with(LocalDensity.current) {
        dialogHeight.toDp()
    }

    giftCategory?.let {
        categoryName = it.name
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() },
                ),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onSizeChanged { dialogHeight = it.height }
                ) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = giftCategory?.let { "선물 카테고리 수정" } ?: "선물 카테고리 등록",
                                style = SentyTheme.typography.headlineSmall,
                            )
                        },
                        actions = {
                            IconButton(onClick = { onDismiss() }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SentyTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        text = categoryName,
                        onChangeText = {
                            categoryName = if (it.length <= 10) {
                                isError = false
                                it
                            } else {
                                it.substring(0, 10)
                            }
                        },
                        hint = "카테고리명 입력 (최대 10자)",
                        errorMsg = "카테고리명을 입력해주세요.",
                        isError = isError,
                        textStyle = SentyTheme.typography.bodyMedium,
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    SentyFilledButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp),
                        text = giftCategory?.let { "수정" } ?: "등록"
                    ) {
                        focusManager.clearFocus()

                        if (categoryName.isEmpty()) {
                            isError = true
                            return@SentyFilledButton
                        }

                        giftCategory?.let {
                            onComplete(it.copy(name = categoryName))
                        } ?: run {
                            onComplete(GiftCategoryUiModel(name = categoryName))
                        }
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(dialogHeightDp)
                        .background(SentyBlack.copy(0.2f), RoundedCornerShape(12.dp))) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .dropShadow(
                                    shape = RoundedCornerShape(16.dp),
                                    offsetX = 0.dp,
                                    offsetY = 0.dp,
                                    blur = 8.dp
                                )
                                .background(SentyWhite, RoundedCornerShape(16.dp))
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GiftCategoryAddDialogPreview() {
    SentyTheme {
        EditGiftCategoryDialog(
            isLoading = true,
            giftCategory = null,
            onDismiss = {},
            onComplete = {}
        )
    }
}