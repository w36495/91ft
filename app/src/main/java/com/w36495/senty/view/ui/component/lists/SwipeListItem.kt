package com.w36495.senty.view.ui.component.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.screen.gift.category.model.GiftCategoryUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeListItem(
    modifier: Modifier = Modifier,
    category: GiftCategoryUiModel,
    onRemove: (GiftCategoryUiModel) -> Unit,
    onEdit: (GiftCategoryUiModel) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeState = rememberSwipeableState(initialValue = 0)

    val squareSize = 112.dp
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, -sizePx to 1)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .height(IntrinsicSize.Min)
            .swipeable(
                state = swipeState,
                orientation = Orientation.Horizontal,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                velocityThreshold = 1000.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.CenterEnd),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color(0xFFF5A61D))
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            swipeState.animateTo(0)
                        }

                        onEdit(category)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color(0xFFF5A61D),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(Color.Red)
                    .fillMaxHeight()
                    .padding(horizontal = 4.dp)
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            swipeState.animateTo(0)
                        }

                        onRemove(category)
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Red,
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        ListItem(
            modifier = Modifier.offset {
                IntOffset(swipeState.offset.value.toInt(), 0)
            },
            headlineContent = {
                Text(
                    text = category.name,
                    style = SentyTheme.typography.bodyMedium,
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.White,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SentyTheme {
        SwipeListItem(
            onRemove = {},
            onEdit = {},
            category = GiftCategoryUiModel("취업취업")
        )
    }
}