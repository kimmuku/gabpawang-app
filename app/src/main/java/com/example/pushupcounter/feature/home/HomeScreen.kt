package com.example.pushupcounter.feature.home

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.STAGE_NAMES
import com.example.pushupcounter.nextThresholdFor
import com.example.pushupcounter.thresholdFor
import com.example.pushupcounter.ui.components.BottomNav
import com.example.pushupcounter.ui.components.BtnPrimary
import com.example.pushupcounter.ui.components.GabpaChar
import com.example.pushupcounter.ui.components.GabpaProgressBar
import com.example.pushupcounter.ui.components.StatCard
import com.example.pushupcounter.ui.components.StatusBarSpacer
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BgDark
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.GreenAccent
import com.example.pushupcounter.ui.theme.Orange
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun HomeScreen(
    charStage: Int,
    totalPushups: Int,
    streak: Int,
    onNav: (String) -> Unit,
    onStartWorkout: () -> Unit,
    onNotif: () -> Unit,
    onCharacter: () -> Unit,
    onStatClick: (String) -> Unit
) {
    val curThreshold = thresholdFor(charStage)
    val nextThreshold = nextThresholdFor(charStage)
    val progressIntoStage = (totalPushups - curThreshold).coerceAtLeast(0)
    val stageRange = (nextThreshold - curThreshold).coerceAtLeast(1)
    val remaining = (nextThreshold - totalPushups).coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(modifier = Modifier.fillMaxSize()) {
            StatusBarSpacer()
            // Top header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "안녕하세요 💪",
                        color = TextSub,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "오늘도 갑빠 만들기",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BgCard)
                        .clickable { onNotif() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "알림",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    // Unread dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Orange)
                            .align(Alignment.TopEnd)
                    )
                }
            }

            // Center character + progress
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0x29FFC400), Color(0x00FFC400)),
                            radius = 600f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    val transition = rememberInfiniteTransition(label = "float")
                    val offsetY by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = -8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3000),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "floatY"
                    )
                    Box(
                        modifier = Modifier
                            .clickable { onCharacter() }
                            .graphicsLayer { translationY = offsetY }
                    ) {
                        GabpaChar(stage = charStage, sizeDp = 140.dp, glow = true)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${charStage}단계 · ${STAGE_NAMES[charStage]}",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${charStage}단계", color = TextPrimary, fontSize = 12.sp)
                        Text(
                            text = "레벨업까지 ${remaining}개",
                            color = Yellow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(charStage + 1).coerceAtMost(9)}단계 🔒",
                            color = TextSub,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    GabpaProgressBar(
                        value = progressIntoStage.toFloat(),
                        max = stageRange.toFloat(),
                        height = 8.dp
                    )
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    value = "32",
                    label = "1회 최고기록",
                    color = Yellow,
                    onClick = { onStatClick("1rm") },
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "$totalPushups",
                    label = "누적 횟수",
                    color = TextPrimary,
                    onClick = { onStatClick("stats") },
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    value = "${streak}일",
                    label = "연속 운동",
                    color = GreenAccent,
                    onClick = { onStatClick("calendar") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                BtnPrimary(text = "운동 시작 💪", onClick = onStartWorkout)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BottomNav(active = "home", onNav = onNav)
        }
    }
}
