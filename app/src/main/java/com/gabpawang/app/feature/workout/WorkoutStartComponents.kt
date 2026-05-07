package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow

internal data class ModeOption(val id: String, val emoji: String, val title: String, val desc: String)

internal val modes = listOf(
    ModeOption("free", "🔥", "자유 모드", "원하는 만큼 운동하세요"),
    ModeOption("target", "🎯", "목표 모드", "세트별 목표 개수 설정"),
    ModeOption("timed", "⏱️", "타임어택", "시간 설정 후 최대 횟수 측정"),
    ModeOption("challenge", "💯", "챌린지 모드", "100개 한 번에 도전")
)

@Composable
internal fun ModeCard(option: ModeOption, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    val bg = if (selected) Yellow.copy(alpha = 0.10f) else colors.bgCard
    val border = if (selected) Yellow else colors.borderCard
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
                color = if (selected) colors.accent else colors.textPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = option.desc, color = colors.textSub, fontSize = 12.sp)
        }
    }
}

@Composable
internal fun SetChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    val bg = if (selected) Yellow else colors.bgCard
    val fg = if (selected) Color.Black else colors.textPrimary
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, if (selected) Yellow else colors.borderCard, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = fg, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun PresetChip(text: String, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.bgCard)
            .border(1.dp, colors.borderCard, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = colors.accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
internal fun CounterBtn(label: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    val bg = if (enabled) colors.bgCard else colors.bgCard.copy(alpha = 0.3f)
    val fg = if (enabled) colors.textPrimary else colors.textSub.copy(alpha = 0.3f)
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, colors.borderCard, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = fg, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
