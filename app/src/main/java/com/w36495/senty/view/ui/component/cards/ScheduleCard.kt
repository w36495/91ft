package com.w36495.senty.view.ui.component.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.w36495.senty.util.dropShadow
import com.w36495.senty.view.screen.anniversary.model.ScheduleUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyWhite

@Composable
fun ScheduleCard(
    schedule: ScheduleUiModel,
    onClickSchedule: (ScheduleUiModel) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(10.dp),
                offsetX = 0.dp,
                offsetY = 0.dp,
                blur = 4.dp,
            )
            .background(
                color = SentyWhite,
                shape = RoundedCornerShape(10.dp),
            )
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClickSchedule(schedule) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 28.dp)
                    .background(SentyGreen60, RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = schedule.title,
                style = SentyTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleCardPreview() {
    SentyTheme {
        ScheduleCard(
            schedule = ScheduleUiModel(
                title = "집들이", date = "2024-06-01",
                time = "",
                location = "",
                memo = ""
            ),
            onClickSchedule = {}
        )
    }
}