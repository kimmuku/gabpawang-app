package com.example.pushupcounter.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.WorkoutConfig
import com.example.pushupcounter.ui.components.BtnPrimary
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.GreenAccent
import com.example.pushupcounter.ui.theme.RedAlert
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

/** Pre-workout instructional view; user taps "운동 시작하기" to enter the running state. */
@Composable
fun ReadyView(config: WorkoutConfig, onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "카메라 준비 중",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "푸쉬업 자세를 잡고 화면에 어깨가 보이게 해주세요.",
                color = TextSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "모드: ${displayMode(config.mode)}",
                color = Yellow,
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BgCard)
                .border(1.dp, BorderCard, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 80.sp)
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            BtnPrimary(text = "운동 시작하기 💪", onClick = onStart)
        }
    }
}

private fun displayMode(mode: String): String = when (mode) {
    "free" -> "자유"
    "target" -> "목표"
    "challenge" -> "챌린지 100"
    else -> mode
}

/** Workout running view: shows live counter, set badges, action buttons, and rest overlay. */
@Composable
fun RunningView(
    config: WorkoutConfig,
    repCount: Int,
    currentSet: Int,
    setHistory: List<Int>,
    inRest: Boolean,
    restRemaining: Int,
    onCompleteSet: () -> Unit,
    onFinishAll: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        if (setHistory.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                setHistory.forEachIndexed { i, n ->
                    SetBadge(text = "${i + 1}세트 ${n}")
                }
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val display = when (config.mode) {
                    "target" -> {
                        val target = config.targetCounts.getOrElse(currentSet - 1) { 20 }
                        (target - repCount).coerceAtLeast(0).toString()
                    }
                    "challenge" -> (100 - repCount).coerceAtLeast(0).toString()
                    else -> repCount.toString()
                }
                val label = when (config.mode) {
                    "target" -> "${currentSet}세트 남은 횟수"
                    "challenge" -> "100개 챌린지"
                    else -> "${currentSet}세트"
                }
                Text(label, color = TextSub, fontSize = 14.sp)
                Text(
                    text = display,
                    color = Yellow,
                    fontSize = 110.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 120.sp
                )
                Text("개", color = TextSub, fontSize = 18.sp)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (config.mode == "challenge") {
                BtnPrimary(text = "포기하기", onClick = onFinishAll, color = RedAlert)
            } else {
                BtnPrimary(text = "세트 완료 ✓", onClick = onCompleteSet, color = GreenAccent)
                BtnPrimary(text = "운동 완료", onClick = onFinishAll)
            }
        }
    }

    if (inRest) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "휴식 시간",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "${restRemaining}",
                    color = Yellow,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text("초 후 다음 세트", color = TextSub, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun SetBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(BgCard)
            .border(1.dp, BorderCard, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = TextPrimary, fontSize = 11.sp)
    }
}
