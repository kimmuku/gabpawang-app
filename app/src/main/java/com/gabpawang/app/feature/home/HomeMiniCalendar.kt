package com.gabpawang.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Compact calendar strip showing workout sessions for the current month.
 * Tapping any day cell navigates to the full record screen.
 */
@Composable
internal fun MiniCalendar(
    sessionsByDate: Map<String, Int>,
    onDayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val cal = remember { Calendar.getInstance() }
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val today = cal.get(Calendar.DAY_OF_MONTH)
    val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val monthLabel = remember {
        SimpleDateFormat("yyyy년 M월", Locale.getDefault()).format(cal.time)
    }
    val firstDayOfWeek = remember {
        Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
            .get(Calendar.DAY_OF_WEEK) - 1
    }
    val fmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    fun dateStr(day: Int): String {
        val c = Calendar.getInstance(); c.set(year, month, day); return fmt.format(c.time)
    }

    val padded = List(firstDayOfWeek) { 0 } + (1..maxDay).toList()
    val full = padded + List((7 - padded.size % 7) % 7) { 0 }
    val rows = full.chunked(7)

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = monthLabel,
            color = colors.textPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 1.dp)
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { label ->
                Text(
                    text = label, color = colors.textSub, fontSize = 7.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                row.forEach { d ->
                    if (d == 0) {
                        Spacer(modifier = Modifier.weight(1f).height(14.dp))
                    } else {
                        val count = sessionsByDate[dateStr(d)] ?: 0
                        val intensity = (count / 50f).coerceIn(0f, 1f)
                        val bg = if (count > 0) Yellow.copy(alpha = 0.15f + intensity * 0.55f) else colors.bgCard
                        val textColor = if (count > 0) Color.Black else colors.textSub
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(bg)
                                .border(
                                    width = if (d == today) 1.5.dp else 0.dp,
                                    color = if (d == today) Yellow else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clickable { onDayClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$d",
                                color = textColor,
                                fontSize = 7.sp,
                                fontWeight = if (d == today) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
