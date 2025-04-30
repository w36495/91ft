package com.w36495.senty.view.ui.component.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.R
import com.w36495.senty.view.screen.home.model.HomeScheduleUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGray10
import com.w36495.senty.view.ui.theme.SentyGray50
import com.w36495.senty.view.ui.theme.SentyGray60
import com.w36495.senty.view.ui.theme.SentyGreen60

@Composable
fun ScheduleWithDateList(
    schedules: List<HomeScheduleUiModel>,
) {
    val chunkedSchedules = schedules.chunked(2)

    val pagerState = rememberPagerState {
        chunkedSchedules.size
    }

    Column {
        HorizontalPager(state = pagerState) {page ->
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                chunkedSchedules[page].forEach {
                    ScheduleItem(schedule = it)
                }
            }
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            chunkedSchedules.forEachIndexed { index, _ ->
                PageIndicator(currentIndex = pagerState.currentPage == index)
            }
        }
    }
}

@Composable
private fun PageIndicator(
    currentIndex: Boolean,
) {
    Box(modifier = Modifier
        .padding(start = 4.dp)
        .padding(top = 8.dp)
        .size(6.dp)
        .background(if (currentIndex) SentyGray60 else SentyGray10, CircleShape))
}

@Composable
private fun ScheduleItem(
    modifier: Modifier = Modifier,
    schedule: HomeScheduleUiModel,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Card(
                modifier = Modifier.width(64.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SentyGreen60,
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = schedule.date,
                    style = SentyTheme.typography.labelMedium
                        .copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = when {
                        schedule.count < 0 -> stringResource(id = R.string.home_anniversary_label_before)
                        schedule.count == 0 -> stringResource(id = R.string.home_anniversary_label_today)
                        else -> schedule.count.toString().plus(stringResource(id = R.string.home_anniversary_label_after))
                    },
                    style = SentyTheme.typography.bodySmall,
                    color = SentyGray50,
                )

                Text(
                    text = schedule.title,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    style = SentyTheme.typography.titleMedium,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptySchedulesPreview() {
    SentyTheme {
        Column {
            ScheduleWithDateList(
                schedules = List(9) {
                    HomeScheduleUiModel(
                        date = "01/01",
                        title = "12345678901234567890123456789012345678901234567890",
                        count = -1
                    )
                }
            )
        }
    }
}