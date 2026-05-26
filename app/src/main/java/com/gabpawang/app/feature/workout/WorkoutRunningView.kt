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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.GreenAccent
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.RedAlert
import com.gabpawang.app.ui.theme.Yellow

/** Workout running view: shows live counter, set badges, action buttons, and rest overlay. */
@Composable
fun RunningView(
    config: WorkoutConfig,
    repCount: Int,
    currentSet: Int,
    setHistory: List<Int>,
    inRest: Boolean,
    restRemaining: Int,
    restTotalSec: Int = 60,
    phase: String,
    elapsedSec: Int,
    countdownRemaining: Int = 0,
    clockText: String = "",
    displayTotal: Int = 0,
    nextLevelGoal: Int = 0,
    udtCount: Int = 0,
    udtPhase: String = "down",
    onCompleteSet: () -> Unit,
    onFinishAll: () -> Unit,
    onExtendRest: () -> Unit = {},
    onSkipRest: () -> Unit = {}
) {
    val colors = LocalAppColors.current
    val upLabel = stringResource(R.string.phase_up)
    val downLabel = stringResource(R.string.phase_down)
    val isReady = phase == upLabel || phase == downLabel
    val showStartPrompt = countdownRemaining == 0 && isReady && repCount == 0 && !inRest
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            CountingStatusBadge(phase = phase)
            if (showStartPrompt) {
                Text(
                    text = stringResource(R.string.workout_start_now_prompt),
                    color = GreenAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            if (clockText.isNotEmpty()) {
                Text(
                    text = clockText,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(
                        R.string.workout_accumulated_and_next_level,
                        displayTotal,
                        nextLevelGoal
                    ),
                    color = Yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (setHistory.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    setHistory.forEachIndexed { i, n ->
                        SetBadge(text = stringResource(R.string.workout_set_history_format, i + 1, n))
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                when (config.mode) {
                    "timed" -> TimedModeDisplay(totalSecs = config.timedSecs, elapsedSec = elapsedSec, repCount = repCount)
                    "udt" -> UdtModeDisplay(target = config.udtTarget, count = udtCount, phase = udtPhase)
                    else -> StandardModeDisplay(config = config, repCount = repCount, currentSet = currentSet)
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .offset(y = (-30).dp),
                contentAlignment = Alignment.Center
            ) {
                CheerOverlay(repCount = repCount)
            }
        }

        Column(
            modifier = Modifier.navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (config.mode) {
                "challenge" -> BtnPrimary(
                    text = stringResource(R.string.workout_give_up),
                    onClick = onFinishAll,
                    color = RedAlert
                )
                "timed" -> BtnPrimary(
                    text = stringResource(R.string.workout_stop),
                    onClick = onFinishAll,
                    color = RedAlert
                )
                "udt" -> BtnPrimary(
                    text = stringResource(R.string.workout_give_up),
                    onClick = onFinishAll,
                    color = RedAlert
                )
                else -> {
                    BtnPrimary(
                        text = stringResource(R.string.workout_complete_set),
                        onClick = onCompleteSet,
                        color = GreenAccent
                    )
                    BtnPrimary(
                        text = stringResource(R.string.workout_finish_all),
                        onClick = onFinishAll
                    )
                }
            }
        }
    }

    if (countdownRemaining > 0) {
        CountdownOverlay(countdownRemaining = countdownRemaining)
    }

    if (inRest) {
        RestOverlay(
            colors = colors,
            restRemaining = restRemaining,
            restTotalSec = restTotalSec,
            onSkipRest = onSkipRest,
            onExtendRest = onExtendRest,
            onFinishAll = onFinishAll
        )
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
        Text(stringResource(R.string.workout_remaining_time), color = colors.textSub, fontSize = 14.sp)
        Text(
            text = stringResource(R.string.workout_time_format, mins, secs),
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
        Text(stringResource(R.string.workout_completed_count), color = colors.textSub, fontSize = 15.sp)
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
            "target" -> stringResource(R.string.workout_set_remaining_count_format, currentSet)
            "challenge" -> stringResource(R.string.workout_challenge_100)
            else -> stringResource(R.string.workout_set_label_format, currentSet)
        }
        Text(label, color = colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(
            text = display,
            color = colors.accent,
            fontSize = 110.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 120.sp
        )
        Text(
            stringResource(R.string.common_count_unit),
            color = colors.textPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun UdtModeDisplay(target: Int, count: Int, phase: String) {
    val colors = LocalAppColors.current
    val phaseLabel = if (phase == "up") stringResource(R.string.udt_phase_up)
                     else stringResource(R.string.udt_phase_down)
    val phaseColor = if (phase == "up") GreenAccent else colors.accent
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = phaseLabel,
            color = phaseColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "$count",
            color = colors.accent,
            fontSize = 110.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 120.sp
        )
        Text(
            text = "/ $target",
            color = colors.textSub,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Status pill showing whether the counting algorithm is calibrated and ready. */
@Composable
private fun CountingStatusBadge(phase: String) {
    val upLabel = stringResource(R.string.phase_up)
    val downLabel = stringResource(R.string.phase_down)
    val readyPhase = stringResource(R.string.phase_ready)
    val ready = phase == upLabel || phase == downLabel
    val accentColor = if (ready) GreenAccent else Color(0xFFFF9500)
    val bgColor = if (ready) Color(0xFF0D2B0D) else Color(0xFF2B1900)
    val label = if (ready) {
        stringResource(R.string.workout_counting_ready)
    } else {
        stringResource(R.string.workout_counting_waiting)
    }
    val text = if (!ready && phase != readyPhase) {
        stringResource(R.string.workout_counting_with_phase_format, label, phase)
    } else {
        label
    }

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
            text = text,
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
