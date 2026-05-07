package com.gabpawang.app.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

private val GabpaDarkScheme = darkColorScheme(
    primary = Yellow,
    onPrimary = Color.Black,
    secondary = Orange,
    onSecondary = Color.Black,
    background = DarkAppColors.bgDark,
    onBackground = DarkAppColors.textPrimary,
    surface = DarkAppColors.bgCard,
    onSurface = DarkAppColors.textPrimary,
    surfaceVariant = DarkAppColors.bgSheet,
    onSurfaceVariant = DarkAppColors.textPrimary,
    error = RedAlert,
    onError = Color.White
)

private val GabpaLightScheme = lightColorScheme(
    primary = Yellow,
    onPrimary = Color.Black,
    secondary = Orange,
    onSecondary = Color.Black,
    background = LightAppColors.bgDark,
    onBackground = LightAppColors.textPrimary,
    surface = LightAppColors.bgSheet,
    onSurface = LightAppColors.textPrimary,
    surfaceVariant = LightAppColors.bgSheet,
    onSurfaceVariant = LightAppColors.textPrimary,
    error = RedAlert,
    onError = Color.White
)

@Composable
fun PushUpCounterTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColors else LightAppColors
    val colorScheme = if (darkTheme) GabpaDarkScheme else GabpaLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalAppColors provides appColors,
            LocalTextStyle provides TextStyle(fontFamily = Pretendard)
        ) {
            content()
        }
    }
}
