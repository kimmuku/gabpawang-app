package com.gabpawang.app.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

private val GabpaDarkColors = darkColorScheme(
    primary = Yellow,
    onPrimary = Color.Black,
    secondary = Orange,
    onSecondary = Color.Black,
    background = BgDark,
    onBackground = TextPrimary,
    surface = BgCard,
    onSurface = TextPrimary,
    surfaceVariant = BgSheet,
    onSurfaceVariant = TextPrimary,
    error = RedAlert,
    onError = Color.White
)

@Composable
fun PushUpCounterTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GabpaDarkColors,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(fontFamily = Pretendard)
        ) {
            content()
        }
    }
}
