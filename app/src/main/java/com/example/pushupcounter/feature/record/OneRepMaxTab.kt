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
import com.example.pushupcounter.ui.theme.Orange
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun OneRepMaxTab() {
    val data = listOf(20, 25, 22, 28, 24, 30, 32)
    val labels = listOf("월", "화", "수", "목", "금", "토", "일")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        StatBlock(value = "32", label = "현재 1회 최고기록")
        Spacer(Modifier.height(20.dp))
        Text(
            text = "최근 7일 추이",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        BarChart(data = data, labels = labels, color = Yellow)
        Spacer(Modifier.height(20.dp))
        Text(
            text = "월간 비교",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        BarChart(
            data = listOf(18, 22, 25, 28, 32),
            labels = listOf("1월", "2월", "3월", "4월", "5월"),
            color = Orange
        )
        Spacer(Modifier.height(40.dp))
    }
}
