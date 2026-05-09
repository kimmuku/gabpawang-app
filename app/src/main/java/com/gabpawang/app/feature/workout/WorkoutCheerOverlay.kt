package com.gabpawang.app.feature.workout

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.GreenAccent
import com.gabpawang.app.ui.theme.Yellow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private fun cheerWord(count: Int): String = when {
    count % 50 == 0 -> "LEGENDARY! 🏆"
    count % 20 == 0 -> "INSANE! 💪"
    count % 10 == 0 -> "Amazing! 🔥"
    count % 5 == 0 -> "Great! 👏"
    else -> when ((count - 1) % 4) {
        0 -> "Good!"
        1 -> "Nice!"
        2 -> "YES!"
        else -> "Keep going!"
    }
}

private fun cheerColor(count: Int): Color = when {
    count % 50 == 0 -> Color(0xFFFFD700)
    count % 20 == 0 -> Color(0xFFFF6B00)
    count % 10 == 0 -> Color(0xFFFF9500)
    count % 5 == 0 -> Yellow
    else -> GreenAccent
}

private fun cheerTier(count: Int): Int = when {
    count % 50 == 0 -> 4
    count % 20 == 0 -> 3
    count % 10 == 0 -> 2
    count % 5 == 0 -> 1
    else -> 0
}

/**
 * Firework-burst judgment badge — explosive keyframe overshoot on entry,
 * solid opaque background for full readability on camera feed,
 * launches upward on exit. Zero height when not shown.
 */
@Composable
fun CheerOverlay(repCount: Int) {
    var text by remember { mutableStateOf("") }
    var bgColor by remember { mutableStateOf(GreenAccent) }
    var tier by remember { mutableStateOf(0) }
    var show by remember { mutableStateOf(false) }

    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val transY = remember { Animatable(0f) }

    LaunchedEffect(repCount) {
        if (repCount > 0) {
            text = cheerWord(repCount)
            bgColor = cheerColor(repCount)
            tier = cheerTier(repCount)

            scale.snapTo(0f)
            alpha.snapTo(0f)
            transY.snapTo(0f)
            show = true

            // Firework burst: 0 → overshoot 1.55 → compress 0.82 → rebound 1.10 → settle 1.0
            launch {
                scale.animateTo(1f, animationSpec = keyframes {
                    durationMillis = 480
                    0f at 0
                    1.55f at 110
                    0.82f at 230
                    1.10f at 340
                    1f at 480
                })
            }
            launch {
                alpha.animateTo(1f, animationSpec = tween(50))
            }

            delay(if (tier >= 2) 900L else 680L)

            // Exit: launch upward + fade
            launch {
                transY.animateTo(-72f, animationSpec = tween(260))
            }
            launch {
                alpha.animateTo(0f, animationSpec = tween(220))
            }
            delay(270L)
            show = false
        }
    }

    if (show) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                        this.alpha = alpha.value
                        translationY = transY.value
                        rotationZ = if (tier == 0) -2f else 0f
                    }
                    .clip(RoundedCornerShape(50))
                    .background(bgColor)
                    .padding(horizontal = 22.dp, vertical = 10.dp)
            ) {
                Text(
                    text = text,
                    color = Color(0xFF080E18),
                    fontSize = when (tier) {
                        4 -> 34.sp
                        3 -> 30.sp
                        2 -> 27.sp
                        1 -> 24.sp
                        else -> 21.sp
                    },
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = if (tier >= 2) 0.5.sp else 0.sp
                )
            }
        }
    }
}
