package com.example.pushupcounter.feature.record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.ui.components.SectionTitle
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun StatsTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBlock("847", "누적 푸쉬업", Modifier.weight(1f))
            StatBlock("32", "1회 최고", Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBlock("12일", "연속 기록", Modifier.weight(1f))
            StatBlock("28회", "일평균", Modifier.weight(1f))
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "월별 성장",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        BarChart(
            data = listOf(120, 240, 380, 520, 847),
            labels = listOf("1월", "2월", "3월", "4월", "5월"),
            color = Yellow
        )
        Spacer(Modifier.height(20.dp))
        SectionTitle("TOP 3 기록")
        Top3Item("최다 일일 기록", "5월 3일", "78회")
        Top3Item("최장 운동 시간", "5월 1일", "32분")
        Top3Item("최다 세트 수", "4월 28일", "5세트")
        Spacer(Modifier.height(40.dp))
    }
}
