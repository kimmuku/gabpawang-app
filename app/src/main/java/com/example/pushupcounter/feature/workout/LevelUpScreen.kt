package com.example.pushupcounter.feature.workout

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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.STAGE_BOUNDARIES
import com.example.pushupcounter.STAGE_NAMES
import com.example.pushupcounter.ui.components.BtnGhost
import com.example.pushupcounter.ui.components.BtnPrimary
import com.example.pushupcounter.ui.components.GabpaChar
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BgDark
import com.example.pushupcounter.ui.theme.BgSheet
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.KakaoYellow
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow

@Composable
fun LevelUpScreen(newStage: Int, onNext: () -> Unit) {
    var showShare by remember { mutableStateOf(false) }
    var shared by remember { mutableStateOf(false) }
    val threshold = STAGE_BOUNDARIES.getOrElse(newStage - 1) { 0 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x4DFFC400), Color(0x00FFC400), BgDark),
                    radius = 800f
                )
            )
    ) {
        // Floating particles
        Particles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("⚡ 단계 진화!", color = Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

            // Animated bobbing character
            val transition = rememberInfiniteTransition(label = "lvl")
            val offsetY by transition.animateFloat(
                initialValue = 0f,
                targetValue = -12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1600),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bob"
            )
            Box(modifier = Modifier.graphicsLayer { translationY = offsetY }) {
                GabpaChar(stage = newStage, sizeDp = 160.dp, glow = true)
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "${newStage}단계 달성!",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(STAGE_NAMES[newStage], color = Yellow, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "누적 ${threshold}개 돌파를 축하해요!",
                color = TextSub,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
            BtnPrimary(text = "계속 성장하기 🚀", onClick = onNext)
            Spacer(modifier = Modifier.height(8.dp))
            BtnGhost(text = "💬 카카오로 자랑하기", onClick = { showShare = true })
        }

        if (showShare) {
            ShareSheet(
                stage = newStage,
                shared = shared,
                onClose = { showShare = false; shared = false },
                onShare = { shared = true }
            )
        }
    }
}

@Composable
private fun Particles() {
    val transition = rememberInfiniteTransition(label = "particles")
    val n = 8
    Box(modifier = Modifier.fillMaxSize()) {
        for (i in 0 until n) {
            val delayMs = (i * 280) % 1800
            val rise by transition.animateFloat(
                initialValue = 0f,
                targetValue = -360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 3200, delayMillis = delayMs)
                ),
                label = "p$i"
            )
            val xOffset = ((i * 73) % 320) - 160
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        translationY = rise
                        translationX = xOffset.toFloat()
                        alpha = ((360f + rise) / 360f).coerceIn(0f, 1f)
                    }
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Yellow.copy(alpha = 0.7f))
            )
        }
    }
}

@Composable
private fun ShareSheet(stage: Int, shared: Boolean, onClose: () -> Unit, onShare: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onClose() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(BgSheet)
                .padding(20.dp)
                .clickable(enabled = false) {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp, 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "카카오톡으로 자랑하기",
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Share preview card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BgCard)
                    .border(1.dp, BorderCard, RoundedCornerShape(14.dp))
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GabpaChar(stage = stage, sizeDp = 80.dp, glow = true)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "${stage}단계 ${STAGE_NAMES[stage]}",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("갑빠왕에서 진화했어요!", color = TextSub, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BtnPrimary(
                text = if (shared) "✓ 공유했어요" else "카카오톡으로 보내기",
                onClick = onShare,
                color = if (shared) Yellow.copy(alpha = 0.6f) else KakaoYellow
            )
            Spacer(modifier = Modifier.height(8.dp))
            BtnGhost(text = "이미지로 저장", onClick = {})
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
