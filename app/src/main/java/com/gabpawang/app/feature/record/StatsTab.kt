package com.gabpawang.app.feature.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun StatsTab(uiState: RecordUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Compact 4-stat summary row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CompactStat("${uiState.totalReps}", "누적", Modifier.weight(1f))
            CompactStat("${uiState.oneRepMax}회", "최고", Modifier.weight(1f))
            CompactStat("${uiState.streak}일", "연속", Modifier.weight(1f))
            CompactStat("${uiState.dailyAvg}회", "평균", Modifier.weight(1f))
        }

        Spacer(Modifier.height(14.dp))

        Text("최근 7일 추이", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        BarChart(
            data = uiState.weeklyReps,
            labels = uiState.weeklyLabels,
            color = Yellow,
            showValues = true,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(14.dp))

        Text("월간 비교", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        BarChart(
            data = uiState.monthlyReps,
            labels = uiState.monthlyLabels,
            color = Orange,
            showValues = true,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.height(14.dp))
    }
}

@Composable
private fun CompactStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BgCard)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = Yellow, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = TextSub, fontSize = 10.sp)
    }
}
