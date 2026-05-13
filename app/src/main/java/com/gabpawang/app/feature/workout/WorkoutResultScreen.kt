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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.WorkoutResult
import com.gabpawang.app.nextThresholdFor
import com.gabpawang.app.stageFor
import com.gabpawang.app.thresholdFor
import androidx.compose.ui.graphics.Color
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors

@Composable
fun WorkoutResultScreen(
    result: WorkoutResult,
    charStage: Int,
    totalPushups: Int,
    onSave: (adjustedTotal: Int, adjustedHistory: List<Int>) -> Unit,
    onHome: () -> Unit
) {
    val colors = LocalAppColors.current
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

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            StatusBarSpacer()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(R.string.result_title),
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.result_subtitle), color = colors.textSub, fontSize = 13.sp)

            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                GabpaChar(stage = charStage, sizeDp = 90.dp, glow = true)
                Spacer(modifier = Modifier.width(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$adjustedTotal",
                        color = colors.accent,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.common_count_unit), color = colors.textSub, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SummaryCard("${result.sets}", stringResource(R.string.result_label_sets), Modifier.weight(1f))
                SummaryCard(
                    "${durMin}:${durSec.toString().padStart(2, '0')}",
                    stringResource(R.string.result_label_duration),
                    Modifier.weight(1f)
                )
                SummaryCard("$adjustedTotal", stringResource(R.string.result_label_total), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.result_section_per_set),
                    color = colors.textPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                if (adjustedTotal != result.total) {
                    Text(
                        text = stringResource(R.string.result_ai_measured_format, result.total),
                        color = colors.textSub.copy(alpha = 0.5f),
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
                remaining = remaining
            )

            Spacer(modifier = Modifier.height(24.dp))
            BtnPrimary(
                text = stringResource(R.string.result_save_button),
                onClick = { onSave(adjustedTotal, adjustedHistory) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            BtnPrimary(
                text = stringResource(R.string.result_home_button),
                onClick = onHome,
                color = Color(0xFFCCCCCC)
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SummaryCard(value: String, label: String, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, RoundedCornerShape(14.dp))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = colors.textSub, fontSize = 11.sp)
    }
}

@Composable
private fun SetRow(idx: Int, count: Int, onMinus: () -> Unit, onPlus: () -> Unit) {
    val colors = LocalAppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            stringResource(R.string.result_set_index_format, idx),
            color = colors.textSub,
            fontSize = 13.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            AdjustBtn("−", enabled = count > 0, onClick = onMinus)
            Text(
                text = stringResource(R.string.result_set_count_format, count),
                color = colors.textPrimary,
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
private fun AdjustBtn(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    val fg = if (enabled) colors.textPrimary else colors.textSub.copy(alpha = 0.25f)
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = fg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
