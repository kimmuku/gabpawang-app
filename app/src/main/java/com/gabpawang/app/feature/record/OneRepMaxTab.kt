package com.gabpawang.app.feature.record

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun OneRepMaxTab(uiState: RecordUiState) {
    val hasData = uiState.weeklyReps.any { it > 0 } || uiState.monthlyReps.any { it > 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        StatBlock(value = "${uiState.oneRepMax}회", label = "현재 1회 최고기록")
        Spacer(Modifier.height(20.dp))

        if (!hasData) {
            // Empty state — no workout records yet
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "아직 기록이 없어요",
                    color = TextSub,
                    fontSize = 14.sp
                )
            }
        } else {
            Text(
                text = "최근 7일 추이",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            BarChart(data = uiState.weeklyReps, labels = uiState.weeklyLabels, color = Yellow)
            Spacer(Modifier.height(20.dp))
            Text(
                text = "월간 비교",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(12.dp))
            BarChart(data = uiState.monthlyReps, labels = uiState.monthlyLabels, color = Orange)
        }
        Spacer(Modifier.height(40.dp))
    }
}
