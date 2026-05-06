package com.gabpawang.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R

val Pretendard = FontFamily(
    Font(R.font.pretendard_regular,   weight = FontWeight.Normal,    loadingStrategy = FontLoadingStrategy.Blocking),
    Font(R.font.pretendard_medium,    weight = FontWeight.Medium,    loadingStrategy = FontLoadingStrategy.Blocking),
    Font(R.font.pretendard_bold,      weight = FontWeight.Bold,      loadingStrategy = FontLoadingStrategy.Blocking),
    Font(R.font.pretendard_extrabold, weight = FontWeight.ExtraBold, loadingStrategy = FontLoadingStrategy.Blocking),
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = Pretendard,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall  = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    titleLarge  = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold,      fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Bold,      fontSize = 16.sp),
    titleSmall  = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium,    fontSize = 14.sp),
    labelLarge  = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium,    fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Medium,    fontSize = 12.sp),
    labelSmall  = TextStyle(fontFamily = Pretendard, fontWeight = FontWeight.Normal,    fontSize = 11.sp),
)