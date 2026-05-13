package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.gabpawang.app.R
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.ui.components.BackHeader
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors

@Composable
fun WorkoutStartScreen(
    onBack: () -> Unit,
    onStart: (WorkoutConfig) -> Unit
) {
    val colors = LocalAppColors.current
    var selected by remember { mutableStateOf("free") }
    var sets by remember { mutableStateOf(3) }
    var counts by remember { mutableStateOf(listOf(0, 0, 0)) }
    var selectedSetIdx by remember { mutableStateOf(0) }
    var timedMins by remember { mutableStateOf(2) }
    var udtTarget by remember { mutableStateOf(0) }
    if (selectedSetIdx >= counts.size) selectedSetIdx = 0

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            BackHeader(title = stringResource(R.string.workout_start_title), onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.workout_start_mode_select),
                    color = colors.textPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                for (mode in modes) {
                    ModeCard(
                        option = mode,
                        selected = selected == mode.id,
                        onClick = { selected = mode.id }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (selected == "timed") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.workout_start_time),
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 3, 5, 10).forEach { m ->
                            SetChip(
                                text = stringResource(R.string.common_minutes_format, m),
                                selected = timedMins == m,
                                onClick = { timedMins = m }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.bgCard)
                            .border(1.dp, colors.borderCard, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.workout_start_custom_set),
                            color = colors.textSub,
                            fontSize = 13.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CounterBtn(label = "−", enabled = timedMins > 1) {
                                timedMins = (timedMins - 1).coerceAtLeast(1)
                            }
                            Text(
                                text = stringResource(R.string.common_minutes_format, timedMins),
                                color = colors.accent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.widthIn(min = 52.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            CounterBtn(label = "+", enabled = timedMins < 30) {
                                timedMins = (timedMins + 1).coerceAtMost(30)
                            }
                        }
                    }
                }

                if (selected == "target") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.workout_start_sets_label),
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (n in 1..7) {
                            SetChip(text = "${n}", selected = sets == n, onClick = {
                                sets = n
                                counts = (0 until n).map { counts.getOrElse(it) { 0 } }
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.workout_start_per_set_count),
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(-10, -5, -1).forEach { n ->
                            StepBtn("$n", modifier = Modifier.weight(1f), accent = false) {
                                counts = counts.toMutableList().apply {
                                    this[selectedSetIdx] = (this[selectedSetIdx] + n).coerceAtLeast(0)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        listOf(1, 5, 10).forEach { n ->
                            StepBtn("+$n", modifier = Modifier.weight(1f), accent = true) {
                                counts = counts.toMutableList().apply {
                                    this[selectedSetIdx] = (this[selectedSetIdx] + n).coerceAtMost(200)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    counts.forEachIndexed { idx, c ->
                        val isSelected = idx == selectedSetIdx
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) colors.accent.copy(alpha = 0.15f) else colors.bgCard)
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) colors.accent else colors.borderCard,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedSetIdx = idx }
                                .padding(horizontal = 14.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.workout_start_set_index_format, idx + 1),
                                color = if (isSelected) colors.accent else colors.textPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                            )
                            Text(
                                stringResource(R.string.common_count_format, c),
                                color = colors.accent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }

                if (selected == "udt") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.workout_start_udt_target),
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(-10, -5, -1).forEach { n ->
                            StepBtn("$n", modifier = Modifier.weight(1f), accent = false) {
                                udtTarget = (udtTarget + n).coerceAtLeast(0)
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        listOf(1, 5, 10).forEach { n ->
                            StepBtn("+$n", modifier = Modifier.weight(1f), accent = true) {
                                udtTarget = (udtTarget + n).coerceAtMost(200)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.bgCard)
                            .border(1.dp, colors.borderCard, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.common_count_format, udtTarget),
                            color = colors.accent,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                var guideExpanded by remember { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.bgCard)
                        .border(1.dp, colors.borderCard, RoundedCornerShape(8.dp))
                        .clickable { guideExpanded = !guideExpanded }
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📷", fontSize = 13.sp)
                    Text(
                        stringResource(R.string.workout_start_camera_guide),
                        color = colors.textSub,
                        fontSize = 13.sp
                    )
                    Text(if (guideExpanded) "▲" else "▼", color = colors.textSub, fontSize = 11.sp)
                }
                if (guideExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(R.drawable.pushup_guide),
                        contentDescription = stringResource(R.string.workout_start_camera_guide_cd),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            val canStart = when (selected) {
                "target" -> counts.all { it > 0 }
                "udt" -> udtTarget > 0
                else -> true
            }
            Box(modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
                BtnPrimary(
                    text = stringResource(R.string.common_start),
                    enabled = canStart,
                    onClick = {
                        onStart(
                            WorkoutConfig(
                                mode = selected,
                                targetCounts = counts,
                                targetSets = sets,
                                timedSecs = timedMins * 60,
                                udtTarget = udtTarget
                            )
                        )
                    }
                )
            }
        }
    }
}
