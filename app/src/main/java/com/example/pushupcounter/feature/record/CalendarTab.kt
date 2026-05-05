package com.example.pushupcounter.feature.record

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
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.Orange
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun CalendarTab() {
    val days = (1..30).toList()
    val counts = days.map { (it * 7) % 50 }
    var selected by remember { mutableStateOf(15) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        StreakCard(streak = 12)
        Spacer(Modifier.height(16.dp))
        Text("2026년 5월", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        val padded = days + List(((7 - days.size % 7) % 7)) { 0 }
        val rows = padded.chunked(7)
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
                            DayCell(
                                day = d,
                                count = counts[(d - 1) % counts.size],
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BgCard)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "5월 ${selected}일",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "총 ${counts[(selected - 1) % counts.size]}회 · 3세트",
                    color = TextSub,
                    fontSize = 12.sp
                )
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
                text = "${streak}일 연속 운동 중!",
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
