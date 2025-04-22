package com.w36495.senty.view.screen.gift.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GiftCategorySelectionDialog(
    vm: GiftCategorySelectionViewModel = hiltViewModel(),
    onClickCategory: (GiftCategoryUiModel) -> Unit,
    onClickCategoriesEdit: () -> Unit,
    onDismiss: () -> Unit,
) {
    val categories by vm.categories.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.loadGiftCategories()
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column{
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.category_selection_title),
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

                when {
                    categories.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .background(Color(0xFFFBFBFB))
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = stringResource(id = R.string.category_selection_empty_text),
                                    style = SentyTheme.typography.bodyMedium
                                        .copy(color = SentyGray70),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                )
                            }

                            SentyFilledButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                text = stringResource(id = R.string.category_selection_empty_button_text),
                                onClick = onClickCategoriesEdit
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(360.dp)
                                .padding(bottom = 16.dp),
                        ) {
                            items(categories.size) { index ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onClickCategory(categories[index]) },
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Text(
                                        text = categories[index].name,
                                        style = SentyTheme.typography.bodyMedium,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(vertical = 16.dp, horizontal = 12.dp),
                                    )

                                    if (index != categories.lastIndex) {
                                        HorizontalDivider(
                                            color = SentyGray20,
                                            thickness = 0.5.dp,
                                            modifier = Modifier.padding(horizontal = 4.dp),
                                        )
                                    }
                                }
                            }
                        }

                        SentyFilledButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            text = stringResource(id = R.string.category_selection_edit_button_text),
                            onClick = onClickCategoriesEdit
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GiftCategorySelectionDialogPreview() {
    SentyTheme {
        GiftCategorySelectionDialog(
            onClickCategory = {},
            onClickCategoriesEdit = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyGiftCategorySelectionDialogPreview() {
    SentyTheme {
        GiftCategorySelectionDialog(
            onClickCategory = {},
            onClickCategoriesEdit = {},
            onDismiss = {}
        )
    }
}