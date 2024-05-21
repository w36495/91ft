package com.w36495.senty.view.screen.gift

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.lists.SwipeListItem
import com.w36495.senty.viewModel.GiftCategoryViewModel

@Composable
fun GiftCategoryScreen(
    vm: GiftCategoryViewModel = hiltViewModel(),
    onPressedBack: () -> Unit,
) {
    val categories by vm.categories.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    GiftCategoryContents(
        categories = categories,
        onPressedBack = { onPressedBack() },
        onClickAdd = {
            showDialog = true
        },
        onRemove = { vm.removeCategory(it) },
        onEdit = { }
    )

    if (showDialog) {
        GiftCategoryAddDialogScreen(
            onDismiss = { showDialog = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftCategoryContents(
    categories: List<GiftCategory>,
    onPressedBack: () -> Unit,
    onClickAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onEdit: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "선물 카테고리",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onPressedBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onClickAdd() }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            categories.forEachIndexed { index, category ->
                SwipeListItem(
                    category = category,
                    onRemove = { onRemove(category.id) },
                    onEdit = { onEdit(category.id) }
                )

                if (index != categories.lastIndex) Divider()
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun GiftCategoryPreview() {
    SentyTheme {
        GiftCategoryContents(
            categories = GiftCategory.DEFAULT_CATEGORY,
            onPressedBack = {},
            onClickAdd = {},
            onRemove = {},
            onEdit = {}
        )
    }
}