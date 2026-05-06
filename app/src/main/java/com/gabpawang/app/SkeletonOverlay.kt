package com.gabpawang.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark

private val CONNECTIONS = listOf(
    11 to 12,         // shoulders
    11 to 13, 13 to 15,   // left arm
    12 to 14, 14 to 16,   // right arm
    11 to 23, 12 to 24,   // torso sides
    23 to 24,         // hips
    23 to 25, 25 to 27,   // left leg
    24 to 26, 26 to 28,   // right leg
)

// Landmark indices used for push-up counting (highlighted in a different color)
private val KEY_LANDMARKS = setOf(11, 12, 23, 24)

@Composable
fun SkeletonOverlay(landmarks: List<NormalizedLandmark>?) {
    if (landmarks.isNullOrEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        fun lmOffset(idx: Int): Offset? {
            val lm = landmarks.getOrNull(idx) ?: return null
            if (lm.visibility().orElse(0f) < 0.3f) return null
            // Mirror X: PreviewView mirrors front camera but ImageAnalysis doesn't
            return Offset((1f - lm.x()) * w, lm.y() * h)
        }

        // Skeleton lines
        for ((a, b) in CONNECTIONS) {
            val start = lmOffset(a) ?: continue
            val end = lmOffset(b) ?: continue
            drawLine(
                color = Color(0xCC00E676),
                start = start,
                end = end,
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
        }

        // Landmark dots
        landmarks.forEachIndexed { idx, lm ->
            if (lm.visibility().orElse(0f) < 0.3f) return@forEachIndexed
            val center = Offset((1f - lm.x()) * w, lm.y() * h)
            val isKey = idx in KEY_LANDMARKS
            drawCircle(
                color = if (isKey) Color(0xFFFF4081) else Color(0xCCFFFFFF),
                radius = if (isKey) 9f else 5f,
                center = center
            )
        }
    }
}
