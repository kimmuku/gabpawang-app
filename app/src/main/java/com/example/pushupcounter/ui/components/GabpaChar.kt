package com.example.pushupcounter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The "갑빠" mascot, drawn entirely with Canvas primitives so we don't need vector assets.
 *
 * @param stage 1..9 — controls scale and color saturation.
 * @param sizeDp logical width of the character; the canvas is sized 1.0 x 1.15 of this value.
 * @param glow when true, an outer Yellow halo is drawn behind the body.
 */
@Composable
fun GabpaChar(
    stage: Int,
    sizeDp: Dp,
    glow: Boolean = false,
    modifier: Modifier = Modifier
) {
    val s = stage.coerceIn(1, 9)
    val t = (s - 1) / 8f

    val bodyColor = lerp(Color(0xFFF5A623), Color(0xFF881800), t)
    val bellyColor = lerp(Color(0xFFFDE9C0), Color(0xFFEF8840), t)
    val eyeColor = lerp(Color(0xFF3D2B1F), Color(0xFF020000), t)
    val cheekColor = bodyColor.copy(alpha = 0.55f).let {
        lerp(Color(0xFFFFB07A), Color(0xFFB04020), t)
    }

    Canvas(modifier = modifier.size(sizeDp, sizeDp * 1.15f)) {
        val w = size.width
        val h = size.height
        val m = 0.75f + (s / 9f) * 0.5f // overall scale multiplier 0.83..1.25

        // Glow halo behind the character
        if (glow) {
            val glowRadius = w * 0.55f * m
            drawCircle(
                color = Color(0xFFFFC400).copy(alpha = 0.18f),
                radius = glowRadius,
                center = Offset(w / 2f, h * 0.52f)
            )
            drawCircle(
                color = Color(0xFFFFC400).copy(alpha = 0.10f),
                radius = glowRadius * 1.35f,
                center = Offset(w / 2f, h * 0.52f)
            )
        }

        // Stage 9 — outer dotted ring
        if (s >= 9) {
            drawCircle(
                color = Color(0xFFFFC400).copy(alpha = 0.6f),
                radius = w * 0.50f * m,
                center = Offset(w / 2f, h * 0.55f),
                style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f)))
            )
        }

        // Shadow oval at the bottom
        drawOval(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(w * 0.30f, h * 0.93f),
            size = Size(w * 0.40f, h * 0.04f)
        )

        // Legs
        drawOval(
            color = bodyColor,
            topLeft = Offset(w * (0.42f - 0.04f * m), h * 0.78f),
            size = Size(w * 0.08f * m, h * 0.16f * m)
        )
        drawOval(
            color = bodyColor,
            topLeft = Offset(w * (0.58f - 0.04f * m), h * 0.78f),
            size = Size(w * 0.08f * m, h * 0.16f * m)
        )

        // Feet
        drawOval(
            color = bodyColor.copy(alpha = 0.85f),
            topLeft = Offset(w * (0.40f - 0.05f * m), h * 0.91f),
            size = Size(w * 0.10f * m, h * 0.04f * m)
        )
        drawOval(
            color = bodyColor.copy(alpha = 0.85f),
            topLeft = Offset(w * (0.60f - 0.05f * m), h * 0.91f),
            size = Size(w * 0.10f * m, h * 0.04f * m)
        )

        // Body — torso
        val bodyW = w * 0.55f * m
        val bodyH = h * 0.40f * m
        drawOval(
            color = bodyColor,
            topLeft = Offset(w / 2f - bodyW / 2f, h * 0.50f),
            size = Size(bodyW, bodyH)
        )

        // Belly highlight
        val bellyW = bodyW * 0.65f
        val bellyH = bodyH * 0.65f
        drawOval(
            color = bellyColor,
            topLeft = Offset(w / 2f - bellyW / 2f, h * 0.55f),
            size = Size(bellyW, bellyH)
        )

        // Arms
        val armW = w * 0.10f * m
        val armH = h * 0.28f * m
        drawOval(
            color = bodyColor,
            topLeft = Offset(w * 0.18f, h * 0.55f),
            size = Size(armW, armH)
        )
        drawOval(
            color = bodyColor,
            topLeft = Offset(w * 0.82f - armW, h * 0.55f),
            size = Size(armW, armH)
        )

        // Neck
        drawOval(
            color = bodyColor,
            topLeft = Offset(w * 0.45f, h * 0.42f),
            size = Size(w * 0.10f * m, h * 0.10f * m)
        )

        // Head
        val headR = w * 0.22f * m
        drawCircle(
            color = bodyColor,
            radius = headR,
            center = Offset(w / 2f, h * 0.30f)
        )

        // Ears (triangles)
        val earPath = Path().apply {
            moveTo(w / 2f - headR * 0.7f, h * 0.20f)
            lineTo(w / 2f - headR * 0.95f, h * 0.10f)
            lineTo(w / 2f - headR * 0.45f, h * 0.18f)
            close()
        }
        drawPath(earPath, color = bodyColor)
        val earPath2 = Path().apply {
            moveTo(w / 2f + headR * 0.7f, h * 0.20f)
            lineTo(w / 2f + headR * 0.95f, h * 0.10f)
            lineTo(w / 2f + headR * 0.45f, h * 0.18f)
            close()
        }
        drawPath(earPath2, color = bodyColor)

        // Cheeks
        drawOval(
            color = cheekColor,
            topLeft = Offset(w / 2f - headR * 0.85f, h * 0.32f),
            size = Size(headR * 0.40f, headR * 0.30f)
        )
        drawOval(
            color = cheekColor,
            topLeft = Offset(w / 2f + headR * 0.45f, h * 0.32f),
            size = Size(headR * 0.40f, headR * 0.30f)
        )

        // Eyes
        val eyeR = headR * 0.13f
        drawCircle(eyeColor, eyeR, Offset(w / 2f - headR * 0.35f, h * 0.28f))
        drawCircle(eyeColor, eyeR, Offset(w / 2f + headR * 0.35f, h * 0.28f))
        // Eye highlights
        drawCircle(Color.White, eyeR * 0.4f, Offset(w / 2f - headR * 0.32f, h * 0.275f))
        drawCircle(Color.White, eyeR * 0.4f, Offset(w / 2f + headR * 0.38f, h * 0.275f))

        // Nose
        drawOval(
            color = eyeColor,
            topLeft = Offset(w / 2f - headR * 0.10f, h * 0.32f),
            size = Size(headR * 0.20f, headR * 0.12f)
        )

        // Crown for stage 7+
        if (s >= 7) {
            val crownPath = Path().apply {
                val cy = h * 0.10f
                val cx = w / 2f
                val cw = headR * 1.4f
                moveTo(cx - cw / 2f, cy + headR * 0.20f)
                lineTo(cx - cw / 2f, cy)
                lineTo(cx - cw * 0.25f, cy + headR * 0.15f)
                lineTo(cx, cy - headR * 0.10f)
                lineTo(cx + cw * 0.25f, cy + headR * 0.15f)
                lineTo(cx + cw / 2f, cy)
                lineTo(cx + cw / 2f, cy + headR * 0.20f)
                close()
            }
            drawPath(crownPath, color = Color(0xFFFFD740))
            // crown gem
            drawCircle(
                color = Color(0xFFFF6060),
                radius = headR * 0.08f,
                center = Offset(w / 2f, h * 0.10f + headR * 0.05f)
            )
        }

        // Stage 9 — sparkles
        if (s >= 9) {
            rotate(degrees = 0f) {
                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.85f),
                    radius = 3f,
                    center = Offset(w * 0.18f, h * 0.40f)
                )
                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.85f),
                    radius = 3f,
                    center = Offset(w * 0.82f, h * 0.45f)
                )
                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.85f),
                    radius = 2f,
                    center = Offset(w * 0.25f, h * 0.20f)
                )
            }
        }
    }
}

@Composable
fun GabpaCharSmall(stage: Int, modifier: Modifier = Modifier) {
    GabpaChar(stage = stage, sizeDp = 40.dp, glow = false, modifier = modifier)
}
