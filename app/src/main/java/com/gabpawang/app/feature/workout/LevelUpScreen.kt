package com.gabpawang.app.feature.workout

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.STAGE_BOUNDARIES
import com.gabpawang.app.STAGE_NAMES
import com.gabpawang.app.STAGE_SUBTITLES
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun LevelUpScreen(newStage: Int, onNext: () -> Unit) {
    val colors = LocalAppColors.current
    val threshold = STAGE_BOUNDARIES.getOrElse(newStage - 1) { 0 }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgDark)
    ) {
        Particles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("⚡ 단계 진화!", color = colors.accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))

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
                GabpaChar(stage = newStage, sizeDp = 160.dp)
            }

            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "${newStage}단계 달성!",
                color = colors.textPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(STAGE_NAMES[newStage], color = colors.accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(STAGE_SUBTITLES[newStage], color = colors.textSub, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "누적 ${threshold}개 돌파를 축하해요!",
                color = colors.textSub,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
            BtnPrimary(text = "계속 성장하기 🚀", onClick = onNext)
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
