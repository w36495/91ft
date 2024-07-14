package com.w36495.senty.view.ui.component.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.w36495.senty.view.screen.anniversary.CalendarRange
import com.w36495.senty.view.ui.component.buttons.SentyFilledButton
import com.w36495.senty.view.ui.theme.Green40
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateWheelPickerDialog(
    modifier: Modifier = Modifier,
    initialYear: Int,
    initialMonth: Int,
    onDismiss: () -> Unit,
    onSelectedDate: (Int, Int) -> Unit,
) {
    val yearRange = (CalendarRange.START_YEAR..CalendarRange.LAST_YEAR).toList()
    val monthRange = (CalendarRange.START_MONTH..CalendarRange.LAST_MONTH).toList()
    var currentYear by remember { mutableIntStateOf(initialYear) }
    var currentMonth by remember { mutableIntStateOf(initialMonth) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CenterAlignedTopAppBar(
                    title = { },
                    actions = {
                        IconButton(onClick = { onDismiss() }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = null)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    DateWheelPicker(
                        startIndex = yearRange.indexOf(initialYear),
                        items = yearRange,
                        modifier = Modifier.weight(1f),
                        onSelectedItem = { selectedYear ->
                            currentYear = selectedYear
                        }
                    )

                    Text(
                        text = "년",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                    )

                    DateWheelPicker(
                        startIndex = monthRange.indexOf(initialMonth),
                        items = monthRange,
                        modifier = Modifier.weight(0.5f),
                        dividerHorizontalPadding = 8.dp,
                        onSelectedItem = { selectedMonth ->
                            currentMonth = selectedMonth
                        }
                    )

                    Text(
                        text = "월",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.5f)
                            .padding(start = 8.dp)
                            .align(Alignment.CenterVertically),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                    )
                }

                SentyFilledButton(
                    text = "선택",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onClick = { onSelectedDate(currentYear, currentMonth) },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DateWheelPicker(
    modifier: Modifier = Modifier,
    pickerState: PickerState = rememberPickerState(),
    items: List<Int>,
    startIndex: Int = 0,
    visibleItemCount: Int = 5,
    dividerColor: Color = Green40,
    dividerHorizontalPadding: Dp = 16.dp,
    onSelectedItem: @Composable (Int) -> Unit,
) {
    val visibleItemsMiddle = visibleItemCount / 2
    val listScrollCount = Int.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val listStartIndex = listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex

    fun getItem(index: Int) = items[index % items.size]

    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = scrollState)

    var itemHeightPixel by remember { mutableIntStateOf(0) }
    val itemHeightToDp = pixelsToDp(pixels = itemHeightPixel)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item ->
                pickerState.selectedItem = item
            }
    }

    Box(modifier = modifier.wrapContentWidth()) {
        LazyColumn(
            state = scrollState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightToDp * visibleItemCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(listScrollCount) { index ->
                Text(
                    text = getItem(index).toString(),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .onSizeChanged { size -> itemHeightPixel = size.height }
                        .padding(vertical = 4.dp)
                )

            }
        }
        HorizontalDivider(
            color = dividerColor,
            thickness = 2.dp,
            modifier = Modifier
                .padding(horizontal = dividerHorizontalPadding)
                .offset(y = itemHeightToDp * visibleItemsMiddle)
        )
        HorizontalDivider(
            color = dividerColor,
            thickness = 2.dp,
            modifier = Modifier
                .padding(horizontal = dividerHorizontalPadding)
                .offset(y = itemHeightToDp * (visibleItemsMiddle + 1))
        )
    }

    onSelectedItem(pickerState.selectedItem)
}

class PickerState {
    var selectedItem by mutableIntStateOf(0)
}

@Composable
private fun rememberPickerState() = remember { PickerState() }

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }