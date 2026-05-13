package com.gabpawang.app.feature.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.Yellow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onNext()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.splash_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

internal data class Slide(val stage: Int, val titleResId: Int, val descResId: Int)

internal val slides = listOf(
    Slide(2, R.string.onboarding_slide_1_title, R.string.onboarding_slide_1_desc),
    Slide(5, R.string.onboarding_slide_2_title, R.string.onboarding_slide_2_desc),
    Slide(8, R.string.onboarding_slide_3_title, R.string.onboarding_slide_3_desc)
)

@Composable
fun OnboardingScreen(onNext: () -> Unit) {
    val colors = LocalAppColors.current
    var page by remember { mutableStateOf(0) }
    val slide = slides[page]

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(
            modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusBarSpacer()
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.onboarding_skip),
                    color = colors.textSub,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNext() }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            GabpaChar(stage = slide.stage, sizeDp = 140.dp, glow = true)
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = stringResource(slide.titleResId),
                color = colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(slide.descResId),
                color = colors.textSub,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            // Page indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                slides.forEachIndexed { i, _ ->
                    Box(
                        modifier = Modifier
                            .size(if (i == page) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (i == page) Yellow else Color.White.copy(alpha = 0.2f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            BtnPrimary(
                text = if (page == slides.lastIndex) {
                    stringResource(R.string.onboarding_start)
                } else {
                    stringResource(R.string.common_next)
                },
                onClick = {
                    if (page == slides.lastIndex) onNext()
                    else page++
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
