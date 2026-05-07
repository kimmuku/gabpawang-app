package com.gabpawang.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Accent colors (theme-invariant)
val Yellow = Color(0xFFFFC400)
val Orange = Color(0xFFFF8C00)
val GreenAccent = Color(0xFF00C853)
val BlueAccent = Color(0xFF00C3FF)
val RedAlert = Color(0xFFFF6060)

// Brand button colors (3rd-party signups)
val KakaoYellow = Color(0xFFFEE500)
val NaverGreen = Color(0xFF03C75A)
val GoogleWhite = Color(0xFFFFFFFF)

data class AppColors(
    val bgDark: Color,
    val bgSheet: Color,
    val bgCard: Color,
    val textPrimary: Color,
    val textSub: Color,
    val borderCard: Color,
    val accent: Color
)

val DarkAppColors = AppColors(
    bgDark = Color(0xFF0A0A12),
    bgSheet = Color(0xFF1A1A26),
    bgCard = Color(0x0DFFFFFF),
    textPrimary = Color(0xFFFFFFFF),
    textSub = Color(0x66FFFFFF),
    borderCard = Color(0x14FFFFFF),
    accent = Yellow
)

val LightAppColors = AppColors(
    bgDark = Color(0xFFF0F2F5),
    bgSheet = Color(0xFFFFFFFF),
    bgCard = Color(0x0A000000),
    textPrimary = Color(0xFF1A1A2E),
    textSub = Color(0x80000000),
    borderCard = Color(0x1A000000),
    accent = Color(0xFFB45309)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha)
