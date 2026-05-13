package com.gabpawang.app.feature.character

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.STAGE_BOUNDARIES
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.stageName
import com.gabpawang.app.stageSubtitle
import com.gabpawang.app.thresholdFor
import com.gabpawang.app.ui.components.BackHeader
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.GabpaCharSmall
import com.gabpawang.app.ui.components.GabpaProgressBar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun CharacterScreen(charStage: Int, totalPushups: Int, onBack: () -> Unit) {
    val colors = LocalAppColors.current
    var selectedStage by remember { mutableStateOf(charStage) }

    val cur = thresholdFor(charStage)
    val next = nextThresholdFor(charStage)
    val pct = (totalPushups - cur).coerceAtLeast(0)
    val range = (next - cur).coerceAtLeast(1)
    val remaining = (next - totalPushups).coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
        ) {
            StatusBarSpacer()
            BackHeader(
                title = stringResource(R.string.character_title),
                onBack = onBack,
                subtitle = stringResource(R.string.character_total_format, totalPushups)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GabpaChar(stage = selectedStage, sizeDp = 110.dp, glow = true)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(
                        R.string.character_stage_label_format,
                        selectedStage,
                        stageName(selectedStage)
                    ),
                    color = colors.accent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stageSubtitle(selectedStage),
                    color = colors.textSub,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(16.dp))

                when {
                    selectedStage == charStage -> {
                        GabpaProgressBar(value = pct.toFloat(), max = range.toFloat(), height = 8.dp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            stringResource(R.string.character_next_remaining_format, remaining),
                            color = colors.textSub,
                            fontSize = 12.sp
                        )
                    }
                    selectedStage < charStage -> {
                        Text(
                            stringResource(R.string.character_done),
                            color = colors.accent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    else -> {
                        Text(
                            text = stringResource(
                                R.string.character_locked_format,
                                thresholdFor(selectedStage)
                            ),
                            color = colors.textSub,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.character_roadmap),
                color = colors.textPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (s in 1..10) {
                    StageRow(
                        stage = s,
                        threshold = STAGE_BOUNDARIES[s - 1],
                        cur = charStage,
                        selected = s == selectedStage,
                        onClick = { selectedStage = s }
                    )
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StageRow(
    stage: Int,
    threshold: Int,
    cur: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalAppColors.current
    val state = when {
        stage < cur -> "done"
        stage == cur -> "current"
        else -> "future"
    }
    val (icon, txtColor, alpha) = when (state) {
        "done" -> Triple("✅", colors.textPrimary, 1f)
        "current" -> Triple("⚡", colors.accent, 1f)
        else -> Triple("🔒", colors.textSub, 0.55f)
    }
    val borderColor = when {
        selected -> Yellow
        state == "current" -> Yellow.copy(alpha = 0.6f)
        else -> colors.borderCard
    }
    val bgColor = when {
        selected -> Yellow.copy(alpha = 0.10f)
        state == "current" -> colors.bgCard.copy(alpha = 0.20f)
        else -> colors.bgCard.copy(alpha = 0.05f)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.padding(end = 10.dp)) {
            GabpaCharSmall(stage = stage)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.character_row_stage_format, stage, stageName(stage)),
                color = txtColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stageSubtitle(stage),
                color = colors.textSub.copy(alpha = alpha),
                fontSize = 10.sp
            )
            Text(
                text = stringResource(R.string.character_row_threshold_format, threshold),
                color = colors.textSub.copy(alpha = alpha),
                fontSize = 11.sp
            )
        }
        Text(text = icon, fontSize = 18.sp)
    }
}
