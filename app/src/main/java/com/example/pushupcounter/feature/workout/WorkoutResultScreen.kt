package com.example.pushupcounter.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.STAGE_NAMES
import com.example.pushupcounter.WorkoutResult
import com.example.pushupcounter.nextThresholdFor
import com.example.pushupcounter.stageFor
import com.example.pushupcounter.thresholdFor
import com.example.pushupcounter.ui.components.BtnPrimary
import com.example.pushupcounter.ui.components.GabpaChar
import com.example.pushupcounter.ui.components.GabpaProgressBar
import com.example.pushupcounter.ui.components.StatusBarSpacer
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BgDark
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.GreenAccent
import com.example.pushupcounter.ui.theme.Orange
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun WorkoutResultScreen(
    result: WorkoutResult,
    charStage: Int,
    totalPushups: Int,
    onHome: () -> Unit
) {
    val gained = result.total
    val newTotal = totalPushups + gained
    val curStage = stageFor(newTotal)
    val curThreshold = thresholdFor(curStage)
    val nextThreshold = nextThresholdFor(curStage)
    val progressIntoStage = (newTotal - curThreshold).coerceAtLeast(0)
    val stageRange = (nextThreshold - curThreshold).coerceAtLeast(1)
    val remaining = (nextThreshold - newTotal).coerceAtLeast(0)
    val durMin = result.durationSec / 60
    val durSec = result.durationSec % 60

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            StatusBarSpacer()
            Spacer(modifier = Modifier.height(16.dp))
            Text("운동 완료! 🎉", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("오늘의 기록을 확인하세요", color = TextSub, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                GabpaChar(stage = charStage, sizeDp = 90.dp, glow = true)
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${gained}",
                            color = Yellow,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("개", color = TextSub, fontSize = 18.sp)
                    }
                    PrBadge()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            // Summary cards row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard("${result.sets}", "세트", Modifier.weight(1f))
                SummaryCard(
                    "${durMin}:${durSec.toString().padStart(2, '0')}",
                    "운동시간",
                    Modifier.weight(1f)
                )
                SummaryCard("${result.total}", "총 횟수", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("세트별 기록", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                result.history.forEachIndexed { i, n ->
                    SetRow(idx = i + 1, count = n)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            EvolutionCard(
                stage = curStage,
                progressIntoStage = progressIntoStage,
                stageRange = stageRange,
                gained = gained,
                remaining = remaining,
                nextThreshold = nextThreshold
            )

            Spacer(modifier = Modifier.height(24.dp))
            BtnPrimary(text = "홈으로 돌아가기", onClick = onHome)
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PrBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(GreenAccent.copy(alpha = 0.2f))
            .border(1.dp, GreenAccent.copy(alpha = 0.5f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text("🏆 개인 신기록", color = GreenAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SummaryCard(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextSub, fontSize = 11.sp)
    }
}

@Composable
private fun SetRow(idx: Int, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${idx}세트", color = TextSub, fontSize = 13.sp)
        Text("${count}개", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EvolutionCard(
    stage: Int,
    progressIntoStage: Int,
    stageRange: Int,
    gained: Int,
    remaining: Int,
    nextThreshold: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("진화 진행도", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("+${gained}개", color = Orange, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(10.dp))
        GabpaProgressBar(
            value = progressIntoStage.toFloat(),
            max = stageRange.toFloat(),
            extra = gained.toFloat(),
            height = 10.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${stage}단계 · ${STAGE_NAMES[stage]}",
                color = TextSub,
                fontSize = 12.sp
            )
            Text(
                text = "다음까지 ${remaining}개",
                color = Yellow,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
