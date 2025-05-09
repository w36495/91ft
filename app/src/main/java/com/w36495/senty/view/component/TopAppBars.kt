package com.w36495.senty.view.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentyCenterAlignedTopAppBar(
    @StringRes title: Int,
    hasBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = title),
                style = SentyTheme.typography.headlineSmall
                    .copy(color = SentyBlack),
            )
        },
        navigationIcon = {
            if (hasBackButton) {
                IconButton(
                    onClick = onBackPressed,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back Button"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SentyAnnotatedCenterAlignedTopAppBar(
    annotatedString: AnnotatedString,
    hasBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    onBackPressed: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = annotatedString,
                style = SentyTheme.typography.headlineSmall
                    .copy(color = SentyBlack),
            )
        },
        navigationIcon = {
            if (hasBackButton) {
                IconButton(
                    onClick = onBackPressed,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back Button"
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}