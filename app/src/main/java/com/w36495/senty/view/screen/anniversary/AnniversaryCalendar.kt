package com.w36495.senty.view.screen.anniversary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.w36495.senty.view.screen.anniversary.model.ScheduleUiModel
import com.w36495.senty.view.screen.ui.theme.SentyTheme
import com.w36495.senty.view.ui.component.dialogs.DateWheelPickerDialog
import com.w36495.senty.view.ui.theme.SentyGreen50
import com.w36495.senty.view.ui.theme.SentyGreen60
import com.w36495.senty.view.ui.theme.SentyYellow60
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale

@Composable
fun AnniversaryCalendar(
    schedules: List<ScheduleUiModel>,
    onSelectedDate: (Int, Int, Int) -> Unit,
) {
    val initialDate: LocalDate = LocalDate.now()
    var currentDate by remember { mutableStateOf(initialDate) }

    val initialPage = (currentDate.year - CalendarRange.START_YEAR) * 12 + currentDate.monthValue - 1
    var currentPage by remember { mutableIntStateOf(initialPage) }

    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { (CalendarRange.LAST_YEAR - CalendarRange.START_YEAR) * 12 }
    )

    LaunchedEffect(pagerState.currentPage) {
        val changeMonth = (pagerState.currentPage - currentPage).toLong()
        val newDate = currentDate.plusMonths(changeMonth)
        currentDate = newDate
        currentPage = pagerState.currentPage
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        CalendarHeader(
            modifier = Modifier.fillMaxWidth(),
            currentDate = currentDate,
            onClickNextMonth = {
                val changeMonth = currentDate.plusMonths(1)
                currentDate = changeMonth
            },
            onClickPreviousMonth = {
                val changeMonth = currentDate.minusMonths(1)
                currentDate = changeMonth
            },
            onChangeCurrentDate = { year, month ->
                currentDate = LocalDate.of(year, month, currentDate.dayOfMonth)
            }
        )

        CalendarDayOfWeek(
            modifier = Modifier.fillMaxWidth()
        )

        CalendarDays(
            modifier = Modifier.fillMaxWidth(),
            pagerState = pagerState,
            currentDate = currentDate,
            schedules = schedules,
            onSelectedDate = { day ->
                val year = currentDate.year
                val month = currentDate.monthValue
                onSelectedDate(year, month, day)
            }
        )
    }
}

@Composable
private fun CalendarHeader(
    modifier: Modifier = Modifier,
    currentDate: LocalDate,
    onChangeCurrentDate: (Int, Int) -> Unit,
    onClickPreviousMonth: () -> Unit,
    onClickNextMonth: () -> Unit,
) {
    val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월"))
    var showDateWheelPicker by remember { mutableStateOf(false) }

    if (showDateWheelPicker) {
        DateWheelPickerDialog(
            initialYear = currentDate.year,
            initialMonth = currentDate.monthValue,
            onDismiss = { showDateWheelPicker = false },
            onSelectedDate = { year, month ->
                onChangeCurrentDate(year, month)
                showDateWheelPicker = false
            }
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onClickPreviousMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = formattedDate,
                style = SentyTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Rounded.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 2.dp)
                    .clip(CircleShape)
                    .clickable { showDateWheelPicker = true }
            )
        }

        IconButton(onClick = onClickNextMonth) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun CalendarDayOfWeek(
    modifier: Modifier = Modifier,
) {
    val dayOfTheWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    Row(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        dayOfTheWeek.forEach {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = it.getDisplayName(TextStyle.NARROW, Locale.KOREA),
                style = SentyTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (it == DayOfWeek.SUNDAY) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CalendarDays(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    currentDate: LocalDate,
    schedules: List<ScheduleUiModel>,
    onSelectedDate: (Int) -> Unit,
) {
    val days by remember(currentDate) {
        mutableStateOf(IntRange(1, currentDate.lengthOfMonth()).toList())
    }
    val firstDayOfWeek by remember(currentDate) {
        mutableIntStateOf(currentDate.dayOfWeek.value)
    }

    val dayOfSchedules = schedules.filter {
        val (year, month, day) = it.date.split("-").map { it.toInt() }
        year == currentDate.year && month == currentDate.monthValue
    }.map { it.date.split("-").map { it.toInt() }.last() }

    HorizontalPager(
        state = pagerState,
        userScrollEnabled = true
    ) {page ->
        VerticalGrid(
            columns = SimpleGridCells.Fixed(7),
            modifier = modifier.padding(horizontal = 16.dp)
        ) {
            for (i in 1 until firstDayOfWeek) {
                EmptyDayBox()
            }

            days.forEach { day ->
                DayBox(
                    modifier = Modifier.padding(bottom = 8.dp),
                    day = day,
                    isToday = (currentDate.year == LocalDate.now().year && currentDate.monthValue == LocalDate.now().monthValue && day == LocalDate.now().dayOfMonth),
                    hasSchedule = dayOfSchedules.contains(day),
                    onSelectedDate = { onSelectedDate(day) },
                )
            }
        }
    }
}

@Composable
private fun DayBox(
    modifier: Modifier = Modifier,
    day: Int,
    isToday: Boolean,
    hasSchedule: Boolean,
    onSelectedDate: () -> Unit,
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .size(32.dp)
            .background(
                color = if (isToday) SentyGreen60 else if (hasSchedule) SentyYellow60 else Color.Transparent,
                shape = CircleShape
            )
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (hasSchedule) MaterialTheme.colorScheme.onSurface.copy(0.3f) else Color.Transparent
                ),
                shape = CircleShape
            )
            .clip(CircleShape)
            .clickable {
                onSelectedDate()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            textAlign = TextAlign.Center,
            style = SentyTheme.typography.bodySmall,
            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun EmptyDayBox(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

object CalendarRange {
    const val START_YEAR = 1970
    const val LAST_YEAR = 2100
    const val START_MONTH = 1
    const val LAST_MONTH = 12
}