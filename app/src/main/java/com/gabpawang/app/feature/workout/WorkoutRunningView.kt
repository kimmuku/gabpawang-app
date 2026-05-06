package com.gabpawang.app.feature.workout

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
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.GreenAccent
import com.gabpawang.app.ui.theme.RedAlert
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

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
    onFinishAll: () -> Unit,
    onExtendRest: () -> Unit = {},
    onSkipRest: () -> Unit = {}
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

        Column(
            modifier = Modifier.navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                .background(Color(0xFF080E18)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "휴식 시간",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${restRemaining}",
                    color = Yellow,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 110.sp
                )
                Text("초 후 다음 세트", color = TextSub, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(40.dp))
                BtnPrimary(text = "다시 시작 ▶", onClick = onSkipRest, color = GreenAccent)
                Spacer(modifier = Modifier.height(10.dp))
                BtnPrimary(text = "휴식 30초 연장", onClick = onExtendRest, color = TextSub.copy(alpha = 0.3f))
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
