package com.w36495.senty.view.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.ui.theme.SentyTheme

@Composable
fun SettingScreen(
    onClickGiftCategorySetting: () -> Unit,
) {
    SettingScreenContents(
        onClickGiftCategorySetting = onClickGiftCategorySetting
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreenContents(
    modifier: Modifier = Modifier,
    onClickGiftCategorySetting: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "설정") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        backgroundColor = Color.White
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding)) {
            SettingItem(
                modifier = Modifier.fillMaxWidth(),
                title = "선물 카테고리 설정",
                icon = Icons.Default.CardGiftcard,
                onClickItem = { onClickGiftCategorySetting() }
            )
        }
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClickItem: () -> Unit,
) {
    Row(
        modifier = modifier.clickable { onClickItem() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 16.dp)
        )
        IconButton(
            modifier = Modifier.padding(end = 16.dp),
            onClick = { },
            enabled = false
        ) {
            Icon(
                imageVector = icon, contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SentyTheme {
        SettingScreenContents(
            onClickGiftCategorySetting = {}
        )
    }
}