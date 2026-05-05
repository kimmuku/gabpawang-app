package com.example.pushupcounter.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.WorkoutConfig
import com.example.pushupcounter.ui.components.BackHeader
import com.example.pushupcounter.ui.components.BtnPrimary
import com.example.pushupcounter.ui.components.StatusBarSpacer
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BgDark
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

private data class ModeOption(val id: String, val emoji: String, val title: String, val desc: String)

private val modes = listOf(
    ModeOption("free", "🔥", "자유 모드", "원하는 만큼 운동하세요"),
    ModeOption("target", "🎯", "목표 모드", "세트별 목표 개수 설정"),
    ModeOption("challenge", "💯", "챌린지 모드", "100개 한 번에 도전")
)

@Composable
fun WorkoutStartScreen(
    onBack: () -> Unit,
    onStart: (WorkoutConfig) -> Unit
) {
    var selected by remember { mutableStateOf("free") }
    var sets by remember { mutableStateOf(3) }
    var counts by remember { mutableStateOf(listOf(30, 25, 20)) }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            BackHeader(title = "운동 시작", onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .weight(1f)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "모드 선택",
                    color = TextPrimary,
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

                if (selected == "target") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "세트 수: ${sets}",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (n in 1..5) {
                            SetChip(text = "${n}세트", selected = sets == n, onClick = {
                                sets = n
                                counts = (0 until n).map { counts.getOrElse(it) { 20 } }
                            })
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    counts.forEachIndexed { idx, c ->
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Text(
                                text = "${idx + 1}세트: ${c}개",
                                color = TextSub,
                                fontSize = 12.sp
                            )
                            Slider(
                                value = c.toFloat(),
                                onValueChange = { v ->
                                    counts = counts.toMutableList().apply {
                                        this[idx] = v.toInt().coerceIn(5, 100)
                                    }
                                },
                                valueRange = 5f..100f,
                                colors = SliderDefaults.colors(
                                    thumbColor = Yellow,
                                    activeTrackColor = Yellow,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                BtnPrimary(text = "시작", onClick = {
                    onStart(
                        WorkoutConfig(
                            mode = selected,
                            targetCounts = counts,
                            targetSets = sets
                        )
                    )
                })
            }
        }
    }
}

@Composable
private fun ModeCard(option: ModeOption, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Yellow.copy(alpha = 0.10f) else BgCard
    val border = if (selected) Yellow else BorderCard

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = option.emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                color = if (selected) Yellow else TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = option.desc, color = TextSub, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SetChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Yellow else BgCard
    val fg = if (selected) Color.Black else TextPrimary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(text = text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
