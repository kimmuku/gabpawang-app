package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.STAGE_NAMES
import com.gabpawang.app.WorkoutResult
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.stageFor
import com.gabpawang.app.thresholdFor
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.Orange
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun WorkoutResultScreen(
    result: WorkoutResult,
    charStage: Int,
    totalPushups: Int,
    onHome: (adjustedTotal: Int, adjustedHistory: List<Int>) -> Unit
) {
    var adjustedHistory by remember { mutableStateOf(result.history) }
    val adjustedTotal = adjustedHistory.sum()
    val newTotal = totalPushups + adjustedTotal
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
                .navigationBarsPadding()
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$adjustedTotal",
                        color = Yellow,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("개", color = TextSub, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
                SummaryCard("$adjustedTotal", "총 횟수", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("세트별 기록", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (adjustedTotal != result.total) {
                    Text(
                        text = "AI 측정: ${result.total}개",
                        color = TextSub.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                adjustedHistory.forEachIndexed { i, count ->
                    SetRow(
                        idx = i + 1,
                        count = count,
                        onMinus = {
                            if (count > 0) {
                                adjustedHistory = adjustedHistory.toMutableList()
                                    .also { it[i] = count - 1 }
                            }
                        },
                        onPlus = {
                            adjustedHistory = adjustedHistory.toMutableList()
                                .also { it[i] = count + 1 }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            EvolutionCard(
                stage = curStage,
                progressIntoStage = progressIntoStage,
                stageRange = stageRange,
                gained = adjustedTotal,
                remaining = remaining,
                nextThreshold = nextThreshold
            )

            Spacer(modifier = Modifier.height(24.dp))
            BtnPrimary(text = "홈으로 돌아가기", onClick = { onHome(adjustedTotal, adjustedHistory) })
            Spacer(modifier = Modifier.height(40.dp))
        }
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
private fun SetRow(idx: Int, count: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("${idx}세트", color = TextSub, fontSize = 13.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdjustBtn("−", enabled = count > 0, onClick = onMinus)
            Text(
                text = "${count}개",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.widthIn(min = 52.dp),
                textAlign = TextAlign.Center
            )
            AdjustBtn("+", onClick = onPlus)
        }
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

@Composable
private fun AdjustBtn(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    val fg = if (enabled) TextPrimary else TextSub.copy(alpha = 0.25f)
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = fg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
