package com.w36495.senty.view.screen.gift

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.entity.gift.GiftCategory
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.component.textFields.SentyTextField
import com.w36495.senty.viewModel.GiftCategoryViewModel

@Composable
fun GiftCategoryAddDialogScreen(
    vm: GiftCategoryViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
) {
    GiftCategoryAddContents(
        onDismiss = { onDismiss() },
        onClickSave = {
            val category = GiftCategory(name = it)

            vm.saveCategory(category)
            onDismiss()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GiftCategoryAddContents(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClickSave: (String) -> Unit,
) {
    var category by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(
                    title = { Text(text = "선물 카테고리 등록") },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                SentyTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    text = category, hint = "카테고리명을 입력하세요.", errorMsg = ""
                ) {
                    category = it
                }

                Spacer(modifier = Modifier.height(32.dp))

                SentyFilledButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    text = "등록"
                ) { onClickSave(category) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GiftCategoryAddDialogPreview() {
    SentyTheme {
        GiftCategoryAddContents(
            onDismiss = { },
            onClickSave = { }
        )
    }
}