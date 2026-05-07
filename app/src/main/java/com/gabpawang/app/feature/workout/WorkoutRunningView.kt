package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.gabpawang.app.ui.theme.GreenAccent
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.RedAlert
import com.gabpawang.app.ui.theme.Yellow

/** Pre-workout instructional view; user taps "운동 시작하기" to enter the running state. */
@Composable
fun ReadyView(config: WorkoutConfig, onStart: () -> Unit) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "카메라 준비 중",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "푸쉬업 자세를 잡고 화면에 어깨가 보이게 해주세요.",
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "모드: ${displayMode(config.mode)}",
                color = colors.accent,
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                .background(colors.bgCard)
                .border(1.dp, colors.borderCard, androidx.compose.foundation.shape.RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 80.sp)
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            BtnPrimary(text = "운동 시작하기 💪", onClick = onStart)
        }
    }
}

fun displayMode(mode: String): String = when (mode) {
    "free" -> "자유"
    "target" -> "목표"
    "timed" -> "타임어택"
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
    phase: String,
    elapsedSec: Int,
    onCompleteSet: () -> Unit,
    onFinishAll: () -> Unit,
    onExtendRest: () -> Unit = {},
    onSkipRest: () -> Unit = {}
) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            CountingStatusBadge(phase = phase)
            if (setHistory.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    setHistory.forEachIndexed { i, n ->
                        SetBadge(text = "${i + 1}세트 ${n}")
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (config.mode == "timed") {
                TimedModeDisplay(totalSecs = config.timedSecs, elapsedSec = elapsedSec, repCount = repCount)
            } else {
                StandardModeDisplay(config = config, repCount = repCount, currentSet = currentSet)
            }
        }

        Column(
            modifier = Modifier.navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (config.mode) {
                "challenge" -> BtnPrimary(text = "포기하기", onClick = onFinishAll, color = RedAlert)
                "timed" -> BtnPrimary(text = "중단하기", onClick = onFinishAll, color = RedAlert)
                else -> {
                    BtnPrimary(text = "세트 완료 ✓", onClick = onCompleteSet, color = GreenAccent)
                    BtnPrimary(text = "운동 완료", onClick = onFinishAll)
                }
            }
        }
    }

    if (inRest) {
        // Intentional hardcoded dark overlay — full-screen cover during rest period over camera feed
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
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${restRemaining}",
                    color = colors.accent,
                    fontSize = 100.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 110.sp
                )
                Text("초 후 다음 세트", color = colors.textSub, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(40.dp))
                BtnPrimary(text = "다시 시작 ▶", onClick = onSkipRest, color = GreenAccent)
                Spacer(modifier = Modifier.height(10.dp))
                BtnPrimary(text = "휴식 30초 연장", onClick = onExtendRest, color = Color.White)
            }
        }
    }
}

@Composable
private fun TimedModeDisplay(totalSecs: Int, elapsedSec: Int, repCount: Int) {
    val colors = LocalAppColors.current
    val remaining = (totalSecs - elapsedSec).coerceAtLeast(0)
    val mins = remaining / 60
    val secs = remaining % 60
    val timerColor = if (remaining <= 30) RedAlert else colors.accent

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("남은 시간", color = colors.textSub, fontSize = 14.sp)
        Text(
            text = "%d:%02d".format(mins, secs),
            color = timerColor,
            fontSize = 80.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 90.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "$repCount",
            color = colors.textPrimary,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 56.sp
        )
        Text("개 완료", color = colors.textSub, fontSize = 15.sp)
    }
}

@Composable
private fun StandardModeDisplay(config: WorkoutConfig, repCount: Int, currentSet: Int) {
    val colors = LocalAppColors.current
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
        Text(label, color = colors.textSub, fontSize = 14.sp)
        Text(
            text = display,
            color = colors.accent,
            fontSize = 110.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 120.sp
        )
        Text("개", color = colors.textSub, fontSize = 18.sp)
    }
}

/** Status pill showing whether the counting algorithm is calibrated and ready. */
@Composable
private fun CountingStatusBadge(phase: String) {
    val ready = phase == "UP ↑" || phase == "DOWN ↓"
    val accentColor = if (ready) GreenAccent else Color(0xFFFF9500)
    val bgColor = if (ready) Color(0xFF0D2B0D) else Color(0xFF2B1900)
    val label = if (ready) "카운팅 준비 완료" else "카운팅 준비 대기"
    val sub = if (!ready && phase != "준비") "  ·  $phase" else ""

    Row(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
            .background(bgColor)
            .border(
                1.5.dp,
                accentColor.copy(alpha = 0.5f),
                androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(11.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Text(
            text = "$label$sub",
            color = accentColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SetBadge(text: String) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, androidx.compose.foundation.shape.RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = text, color = colors.textPrimary, fontSize = 11.sp)
    }
}
