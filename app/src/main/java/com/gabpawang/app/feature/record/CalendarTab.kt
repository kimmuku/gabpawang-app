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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow
import com.gabpawang.app.data.db.WorkoutSessionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarTab(uiState: RecordUiState) {
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

    val monthLabel = remember {
        SimpleDateFormat("yyyy년 M월", Locale.getDefault()).format(cal.time)
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
        Text(monthLabel, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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

        val selectedSessions = uiState.sessionDetailsByDate[dateStr(selected)] ?: emptyList()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BgCard)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${month + 1}월 ${selected}일",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (selectedSessions.isEmpty()) {
                Text("운동 기록 없음", color = TextSub, fontSize = 12.sp)
            } else {
                selectedSessions.forEachIndexed { sessionIdx, session ->
                    val setCounts = session.setHistory
                        .split(",")
                        .filter { it.isNotBlank() }
                        .mapNotNull { it.toIntOrNull() }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        val modeLabel = when (session.mode) {
                            "free" -> "자유 모드"
                            "target" -> "목표 모드"
                            "challenge" -> "챌린지"
                            else -> null
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedSessions.size > 1) {
                                Text(
                                    text = "운동 ${sessionIdx + 1}",
                                    color = Yellow,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Spacer(Modifier.width(0.dp))
                            }
                            if (modeLabel != null) {
                                Text(
                                    text = modeLabel,
                                    color = TextSub,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        if (setCounts.isNotEmpty()) {
                            setCounts.forEachIndexed { setIdx, reps ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("세트 ${setIdx + 1}", color = TextSub, fontSize = 12.sp)
                                    Text("${reps}회", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("합계", color = TextSub, fontSize = 12.sp)
                            Text("${session.totalReps}회", color = Yellow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun StreakCard(streak: Int) {
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
                text = if (streak > 0) "${streak}일 연속 운동 중!" else "오늘 운동을 시작해요!",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text("계속 이어가세요", color = TextSub, fontSize = 12.sp)
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
    val intensity = (count / 50f).coerceIn(0f, 1f)
    val bg = Yellow.copy(alpha = 0.15f + intensity * 0.55f)
    val borderColor = if (selected) Yellow else Color.Transparent
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (count > 0) bg else BgCard)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$day", color = TextSub, fontSize = 9.sp)
        Text(
            text = if (count > 0) "$count" else "-",
            color = if (count > 0) Color.Black else TextSub,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
