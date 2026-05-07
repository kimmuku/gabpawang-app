package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var counts by remember { mutableStateOf(listOf(30, 25, 20)) }
    var timedMins by remember { mutableStateOf(2) }

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            BackHeader(title = "푸쉬업 시작", onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "모드 선택",
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
                        text = "운동 시간",
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1, 2, 3, 5, 10).forEach { m ->
                            SetChip(text = "${m}분", selected = timedMins == m, onClick = { timedMins = m })
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
                        Text("직접 설정", color = colors.textSub, fontSize = 13.sp)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CounterBtn(label = "−", enabled = timedMins > 1) {
                                timedMins = (timedMins - 1).coerceAtLeast(1)
                            }
                            Text(
                                text = "${timedMins}분",
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
                        text = "세트 수",
                        color = colors.textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (n in 1..5) {
                            SetChip(text = "${n}", selected = sets == n, onClick = {
                                sets = n
                                counts = (0 until n).map { counts.getOrElse(it) { 20 } }
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "세트별 횟수",
                            color = colors.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(10, 20, 30, 50).forEach { preset ->
                                PresetChip(text = "${preset}") {
                                    counts = counts.map { preset }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    counts.forEachIndexed { idx, c ->
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
                            Text("${idx + 1}세트", color = colors.textSub, fontSize = 13.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CounterBtn(label = "−", enabled = c > 1) {
                                    counts = counts.toMutableList().apply { this[idx] = (c - 1).coerceAtLeast(1) }
                                }
                                Text(
                                    text = "${c}개",
                                    color = colors.accent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.widthIn(min = 52.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                CounterBtn(label = "+", enabled = c < 200) {
                                    counts = counts.toMutableList().apply { this[idx] = (c + 1).coerceAtMost(200) }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
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
                    Text("촬영 가이드", color = colors.textSub, fontSize = 13.sp)
                    Text(if (guideExpanded) "▲" else "▼", color = colors.textSub, fontSize = 11.sp)
                }
                if (guideExpanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = painterResource(R.drawable.pushup_guide),
                        contentDescription = "촬영 가이드",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
                BtnPrimary(text = "시작", onClick = {
                    onStart(
                        WorkoutConfig(
                            mode = selected,
                            targetCounts = counts,
                            targetSets = sets,
                            timedSecs = timedMins * 60
                        )
                    )
                })
            }
        }
    }
}

