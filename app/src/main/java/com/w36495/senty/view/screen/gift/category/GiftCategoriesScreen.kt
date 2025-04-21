package com.w36495.senty.view.screen.gift.category

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.R
import com.w36495.senty.view.screen.gift.category.component.EditGiftCategoryDialog
import com.w36495.senty.view.screen.gift.category.contact.GiftCategoryContact
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.component.lists.SwipeListItem
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun GiftCategoriesRoute(
    vm: GiftCategoriesViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                GiftCategoryContact.Effect.NavigateToSettings -> {
                    onBackPressed()
                }
                is GiftCategoryContact.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is GiftCategoryContact.Effect.ShowError -> {

                }
            }
        }
    }

    GiftCategoriesScreen(
        uiState = uiState,
        onBackPressed = { vm.handleEvent(GiftCategoryContact.Event.OnClickBack) },
        onClickAdd = { vm.handleEvent(GiftCategoryContact.Event.OnClickAdd(it)) },
        onClickRemove = { vm.handleEvent(GiftCategoryContact.Event.OnClickDelete(it)) },
        onClickEdit = { vm.handleEvent(GiftCategoryContact.Event.OnClickEdit(it)) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftCategoriesScreen(
    modifier: Modifier = Modifier,
    uiState: GiftCategoryContact.State,
    onBackPressed: () -> Unit,
    onClickAdd: (GiftCategoryUiModel?) -> Unit,
    onClickEdit: (GiftCategoryUiModel?) -> Unit,
    onClickRemove: (String?) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.gift_categories_title),
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onClickAdd(null) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = SentyWhite,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.categories) {category ->
                    SwipeListItem(
                        category = category,
                        onRemove = { onClickRemove(category.id) },
                        onEdit = { onClickEdit(category) }
                    )

                    HorizontalDivider(
                        color = SentyGray20,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                }
            }

            if (uiState.showAddCategoryDialog) {
                EditGiftCategoryDialog(
                    onComplete = { onClickAdd(it) },
                    onDismiss = { onClickAdd(null) },
                )
            }
            if (uiState.showEditCategoryDialog) {
                EditGiftCategoryDialog(
                    giftCategory = uiState.selectedCategory,
                    onComplete = { onClickEdit(it) },
                    onDismiss = { onClickEdit(null) },
                )
            }
            if (uiState.showDeleteCategoryDialog) {
                BasicAlertDialog(
                    title = stringResource(id = R.string.gift_categories_delete_title),
                    message = stringResource(id = R.string.gift_categories_delete_message_text),
                    hasCancel = true,
                    onComplete = { onClickRemove(uiState.selectedCategory?.id) },
                    onDismiss = { onClickRemove(null) }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun GiftCategoryPreview() {
    SentyTheme {
        GiftCategoriesScreen(
            uiState = GiftCategoryContact.State(),
            onBackPressed = {},
            onClickAdd = {},
            onClickEdit = {},
            onClickRemove = {},
        )
    }
}