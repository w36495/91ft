package com.w36495.senty.view.screen.main.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.main.MainBottomTab
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGray20
import com.w36495.senty.view.ui.theme.SentyGray80
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite

private val BOTTOM_BAR_CONTAINER_HEIGHT = 62.dp

@Composable
fun MainBottomTabBar(
    isVisible: Boolean,
    tabs: List<MainBottomTab>,
    currentTab: MainBottomTab?,
    onTabSelected: (MainBottomTab) -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Surface(
            color = SentyWhite,
            modifier = Modifier.navigationBarsPadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BOTTOM_BAR_CONTAINER_HEIGHT)
            ) {
                HorizontalDivider(
                    color = SentyGray20,
                    thickness = 0.5.dp,
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SentyWhite),
                ) {
                    tabs.forEach { tab ->
                        BottomBarItem(
                            modifier = Modifier.weight(1f),
                            tab = tab,
                            selected = tab == currentTab,
                            onClick = { onTabSelected(tab) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    modifier: Modifier = Modifier,
    tab: MainBottomTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                indication = null,
                role = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Icon(
            painter = painterResource(id = tab.icon),
            contentDescription = "Bottom Tab Icon",
            tint = if (selected) SentyGreen60 else SentyGray80,
        )

        Text(
            text = tab.title,
            style = SentyTheme.typography.labelSmall,
            color = if (selected) SentyGreen60 else SentyGray80,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LottoMateBottomBarPreview() {
    SentyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            MainBottomTabBar(
                isVisible = true,
                tabs = listOf(
                    MainBottomTab.HOME,
                    MainBottomTab.FRIEND,
                    MainBottomTab.GIFT_ADD,
                    MainBottomTab.ANNIVERSARY,
                    MainBottomTab.SETTINGS,
                ),
                currentTab = MainBottomTab.HOME,
                onTabSelected = {}
            )
        }
    }
}