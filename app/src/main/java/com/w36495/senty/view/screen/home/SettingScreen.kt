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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.viewModel.AccountViewModel

@Composable
fun SettingScreen(
    vm: AccountViewModel = hiltViewModel(),
    onClickGiftCategorySetting: () -> Unit,
    onSuccessLogout: () -> Unit,
) {
    val logoutResult by vm.logoutResult.collectAsState()
    val deleteUserResult by vm.deleteUserResult.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteUserDialog by remember { mutableStateOf(false) }

    if (logoutResult || deleteUserResult) onSuccessLogout()
    if (showLogoutDialog) {
        BasicAlertDialog(
            title = "로그아웃하시겠습니까?",
            onComplete = {
                vm.userLogout()
                showLogoutDialog = false
                onSuccessLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    } else if (showDeleteUserDialog) {
        BasicAlertDialog(
            title = "계정을 삭제하시겠습니까?",
            discContent = {
                Text(text = "계정을 삭제하면 복구할 수 없습니다.")
            },
            onComplete = {
                vm.deleteUser()
                showDeleteUserDialog = false
                onSuccessLogout()
            },
            onDismiss = { showDeleteUserDialog = false }
        )
    }

    SettingScreenContents(
        onClickGiftCategorySetting = { onClickGiftCategorySetting() },
        onClickLogout = { showLogoutDialog = true },
        onClickDelete = { showDeleteUserDialog = true }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreenContents(
    modifier: Modifier = Modifier,
    onClickGiftCategorySetting: () -> Unit,
    onClickLogout: () -> Unit,
    onClickDelete: () -> Unit,
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

            HorizontalDivider()

            SettingItem(
                modifier = Modifier.fillMaxWidth(),
                title = "로그아웃",
                icon = Icons.AutoMirrored.Filled.Logout,
                onClickItem = { onClickLogout() }
            )

            HorizontalDivider()

            SettingItem(
                modifier = Modifier.fillMaxWidth(),
                title = "회원탈퇴",
                icon = Icons.Default.DeleteForever,
                onClickItem = { onClickDelete() }
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
            onClickGiftCategorySetting = {},
            onClickLogout = {},
            onClickDelete = {}
        )
    }
}