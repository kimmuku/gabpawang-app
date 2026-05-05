package com.example.pushupcounter.ui.theme

import androidx.compose.ui.graphics.Color

// Background colors
val BgDark = Color(0xFF0A0A12)
val BgSheet = Color(0xFF1A1A26)
val BgCard = Color(0x0DFFFFFF)

// Accent colors
val Yellow = Color(0xFFFFC400)
val Orange = Color(0xFFFF8C00)

// Text colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSub = Color(0x66FFFFFF)

// Border / dividers
val BorderCard = Color(0x14FFFFFF)

// Semantic accents
val GreenAccent = Color(0xFF00C853)
val BlueAccent = Color(0xFF00C3FF)
val RedAlert = Color(0xFFFF6060)

// Brand button colors (3rd-party signups)
val KakaoYellow = Color(0xFFFEE500)
val NaverGreen = Color(0xFF03C75A)
val GoogleWhite = Color(0xFFFFFFFF)

// Helper for alpha mutation (used across components)
fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha)
