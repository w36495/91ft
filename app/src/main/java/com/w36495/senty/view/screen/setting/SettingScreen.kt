package com.w36495.senty.view.screen.setting

import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.w36495.senty.BuildConfig
import com.w36495.senty.R
import com.w36495.senty.view.component.LoadingCircleIndicator
import com.w36495.senty.view.screen.setting.model.SettingContact
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.BasicAlertDialog
import com.w36495.senty.view.ui.theme.SentyGray20

@Composable
fun SettingsRoute(
    vm: SettingViewModel = hiltViewModel(),
    padding: PaddingValues,
    moveToLogin: () -> Unit,
    moveToGiftCategories: () -> Unit,
    onShowGlobalErrorSnackBar: (throwable: Throwable?) -> Unit,
) {
    val context = LocalContext.current

    val uiState by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        vm.effect.collect { effect ->
            when (effect) {
                is SettingContact.Effect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                    vm.sendEffect(SettingContact.Effect.Complete)
                }
                is SettingContact.Effect.ShowError -> {
                    onShowGlobalErrorSnackBar(effect.throwable)
                }
                SettingContact.Effect.Complete -> { moveToLogin() }
            }
        }
    }

    SettingsScreen(
        uiState = uiState,
        onClickGiftCategories = moveToGiftCategories,
        onClickSuggestionBox = {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(BuildConfig.SUGGESTION_BOX_URL))
            context.startActivity(intent)
        },
        onClickLogout = { vm.signOut(context) },
        onClickWithDraw = { vm.withDraw(context) },
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    uiState: SettingContact.State,
    onClickGiftCategories: () -> Unit,
    onClickSuggestionBox: () -> Unit,
    onClickLogout: () -> Unit,
    onClickWithDraw: () -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteUserDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        BasicAlertDialog(
            title = stringResource(id = R.string.settings_logout_title),
            hasCancel = true,
            onComplete = {
                onClickLogout()
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    } else if (showDeleteUserDialog) {
        BasicAlertDialog(
            title = stringResource(id = R.string.settings_withdraw_title),
            message = stringResource(id = R.string.settings_withdraw_message_text),
            hasCancel = true,
            onComplete = {
                onClickWithDraw()
                showDeleteUserDialog = false
            },
            onDismiss = { showDeleteUserDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "설정",
                        style = SentyTheme.typography.headlineSmall,
                    )
                },
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
                title = R.string.settings_gift_category_text,
                icon = Icons.Default.CardGiftcard,
                onClickItem = onClickGiftCategories,
            )

            SettingItem(
                modifier = Modifier.fillMaxWidth(),
                title = R.string.settings_suggestion_box_text,
                icon = Icons.Default.DeleteForever,
                onClickItem = onClickSuggestionBox,
            )

            SettingItem(
                modifier = Modifier.fillMaxWidth(),
                title = R.string.settings_logout_text,
                icon = Icons.AutoMirrored.Filled.Logout,
                onClickItem = { showLogoutDialog = true },
            )

            SettingItem(
                modifier = Modifier.fillMaxWidth(),
                title = R.string.settings_withdraw_text,
                icon = Icons.Default.DeleteForever,
                onClickItem = { showDeleteUserDialog = true }
            )
        }
    }

    if (uiState.isLoading) {
        LoadingCircleIndicator()
    }
}

@Composable
private fun SettingItem(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    icon: ImageVector,
    onClickItem: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable { onClickItem() },
    ) {
        Text(
            text = stringResource(id = title),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            style = SentyTheme.typography.bodyMedium,
        )

        HorizontalDivider(
            color = SentyGray20,
            thickness = 0.5.dp,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun SettingScreenPreview() {
    SentyTheme {
        SettingsScreen(
            uiState = SettingContact.State(),
            onClickLogout = {},
            onClickGiftCategories = {},
            onClickSuggestionBox = {},
            onClickWithDraw = {},
        )
    }
}