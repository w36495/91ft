package com.w36495.senty.view.ui.component.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.view.entity.Schedule
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.Green40

@Composable
fun ScheduleWithDateList(
    schedules: List<Schedule>,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        schedules.forEach {
            ScheduleItem(schedule = it)
        }
    }
}

@Composable
private fun ScheduleItem(
    modifier: Modifier = Modifier,
    schedule: Schedule,
) {
    val scheduleDate = schedule.date.split("-")
    val calDate = schedule.calculateDays()
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
                modifier = Modifier,
                colors = CardDefaults.cardColors(
                    containerColor = Green40
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = scheduleDate[1].plus("/${scheduleDate[2]}"),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(12.dp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (calDate == 0) "오늘" else "${calDate}일 후",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFF929292)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = schedule.title,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleWithDateCardPreview() {
    SentyTheme {
        Column {
            ScheduleWithDateList(
                schedules = listOf(
                    Schedule(title = "제목", date = "2024-04-20"),
                    Schedule(title = "제목", date = "2024-04-20"),
                    Schedule(title = "제목", date = "2024-04-20"),
                )
            )

            ScheduleWithDateList(
                schedules = emptyList()
            )

            ScheduleWithDateList(
                schedules = listOf(
                    Schedule(title = "제목", date = "2024-05-29"),
                    Schedule(title = "제목", date = "2024-05-30"),
                    Schedule(title = "제목", date = "2024-04-20"),
                )
            )
        }
    }
}