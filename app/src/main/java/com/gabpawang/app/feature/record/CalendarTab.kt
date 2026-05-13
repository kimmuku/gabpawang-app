package com.gabpawang.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.Yellow
import com.gabpawang.app.data.db.WorkoutSessionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarTab(uiState: RecordUiState) {
    val colors = LocalAppColors.current
    val cal = remember { Calendar.getInstance() }
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) // 0-based
    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Day of week for 1st of current month (1=Sun, 2=Mon, ... 7=Sat → 0-based offset)
    val firstDayCal = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val firstDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sun, 1=Mon,...

    val monthFmtPattern = stringResource(R.string.calendar_month_format)
    val monthLabel = remember(monthFmtPattern) {
        SimpleDateFormat(monthFmtPattern, Locale.getDefault()).format(cal.time)
    }
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var selected by remember { mutableStateOf(cal.get(Calendar.DAY_OF_MONTH)) }

    // Build date string for a given day in current month
    fun dateStr(day: Int): String {
        val c = Calendar.getInstance()
        c.set(year, month, day)
        return fmt.format(c.time)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        StreakCard(streak = uiState.streak)
        Spacer(Modifier.height(16.dp))
        Text(monthLabel, color = colors.textPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // Pad leading empty cells so day 1 aligns to correct weekday column
        val days = (1..maxDay).toList()
        val leadingPad = List(firstDayOfWeek) { 0 }
        val paddedDays = leadingPad + days
        val trailingPad = (7 - paddedDays.size % 7) % 7
        val fullPadded = paddedDays + List(trailingPad) { 0 }
        val rows = fullPadded.chunked(7)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            for (row in rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (d in row) {
                        if (d == 0) {
                            Spacer(modifier = Modifier.weight(1f).height(56.dp))
                        } else {
                            val count = uiState.sessionsByDate[dateStr(d)] ?: 0
                            DayCell(
                                day = d,
                                count = count,
                                selected = d == selected,
                                modifier = Modifier.weight(1f),
                                onClick = { selected = d }
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val selectedSessions = (uiState.sessionDetailsByDate[dateStr(selected)] ?: emptyList())
            .filter { it.totalReps > 0 }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.bgCard)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.calendar_day_format, month + 1, selected),
                color = colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (selectedSessions.isEmpty()) {
                Text(stringResource(R.string.record_no_workout), color = colors.textSub, fontSize = 12.sp)
            } else {
                SessionsTable(sessions = selectedSessions)
                val dayTotal = selectedSessions.sumOf { it.totalReps }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.record_day_total), color = colors.textSub, fontSize = 12.sp)
                    Text(
                        text = stringResource(R.string.record_day_total_value_format, dayTotal),
                        color = colors.accent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StreakCard(streak: Int) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Orange.copy(alpha = 0.15f))
            .border(1.dp, Orange.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔥", fontSize = 32.sp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = if (streak > 0) {
                    stringResource(R.string.record_streak_active_format, streak)
                } else {
                    stringResource(R.string.record_streak_inactive)
                },
                color = colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(stringResource(R.string.record_streak_keep_going), color = colors.textSub, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SessionsTable(sessions: List<WorkoutSessionEntity>) {
    val colors = LocalAppColors.current
    val allSetCounts = sessions.map { s ->
        s.setHistory.split(",").filter { it.isNotBlank() }.mapNotNull { it.toIntOrNull() }
            .ifEmpty { listOf(s.totalReps) }
    }
    val maxSets = allSetCounts.maxOf { it.size }
    val isSingle = sessions.size == 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, colors.borderCard, RoundedCornerShape(8.dp))
    ) {
        // Header: [workout label (multi only)] | Set 1 | Set 2 | ... | Total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgDark)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isSingle) Spacer(modifier = Modifier.weight(1.2f))
            for (setIdx in 0 until maxSets) {
                Text(
                    text = stringResource(R.string.record_set_col_format, setIdx + 1),
                    color = colors.textSub, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                )
            }
            Text(
                text = stringResource(R.string.record_total_col),
                color = colors.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
            )
        }
        Box(Modifier.fillMaxWidth().height(0.5.dp).background(colors.borderCard))

        // One row per session
        sessions.forEachIndexed { idx, s ->
            val counts = allSetCounts[idx]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isSingle) {
                    Text(
                        text = stringResource(R.string.record_session_index_format, idx + 1),
                        color = colors.accent, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.2f)
                    )
                }
                for (setIdx in 0 until maxSets) {
                    val reps = counts.getOrNull(setIdx)
                    Text(
                        text = if (reps != null) {
                            stringResource(R.string.record_reps_format, reps)
                        } else {
                            stringResource(R.string.record_dash)
                        },
                        color = if (reps != null) colors.textPrimary else colors.textSub,
                        fontSize = 13.sp,
                        fontWeight = if (reps != null) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                    )
                }
                Text(
                    text = stringResource(R.string.record_reps_format, s.totalReps),
                    color = colors.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                )
            }
            if (idx < sessions.lastIndex) {
                Box(Modifier.fillMaxWidth().height(0.5.dp).background(colors.borderCard))
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val intensity = (count / 50f).coerceIn(0f, 1f)
    val bg = Yellow.copy(alpha = 0.15f + intensity * 0.55f)
    val borderColor = if (selected) Yellow else Color.Transparent
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (count > 0) bg else colors.bgCard)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$day", color = colors.textSub, fontSize = 9.sp)
        Text(
            text = if (count > 0) "$count" else stringResource(R.string.record_dash),
            color = if (count > 0) Color.Black else colors.textSub,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
