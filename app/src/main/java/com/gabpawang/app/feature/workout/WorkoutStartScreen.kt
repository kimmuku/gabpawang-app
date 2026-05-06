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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.ui.components.BackHeader
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.BgCard
import com.gabpawang.app.ui.theme.BgDark
import com.gabpawang.app.ui.theme.BorderCard
import com.gabpawang.app.ui.theme.TextPrimary
import com.gabpawang.app.ui.theme.TextSub
import com.gabpawang.app.ui.theme.Yellow

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
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
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
                        text = "세트 수",
                        color = TextPrimary,
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
                    Text(
                        text = "세트별 횟수",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    counts.forEachIndexed { idx, c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BgCard)
                                .border(1.dp, BorderCard, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${idx + 1}세트", color = TextSub, fontSize = 13.sp)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CounterBtn(label = "−", enabled = c > 5) {
                                    counts = counts.toMutableList().apply { this[idx] = (c - 5).coerceAtLeast(5) }
                                }
                                Text(
                                    text = "${c}개",
                                    color = Yellow,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.widthIn(min = 52.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                CounterBtn(label = "+", enabled = c < 100) {
                                    counts = counts.toMutableList().apply { this[idx] = (c + 5).coerceAtMost(100) }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Box(modifier = Modifier.navigationBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp)) {
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
            .border(1.dp, if (selected) Yellow else BorderCard, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = fg, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CounterBtn(label: String, enabled: Boolean, onClick: () -> Unit) {
    val bg = if (enabled) BgCard else BgCard.copy(alpha = 0.3f)
    val fg = if (enabled) TextPrimary else TextSub.copy(alpha = 0.3f)
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, BorderCard, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = fg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
