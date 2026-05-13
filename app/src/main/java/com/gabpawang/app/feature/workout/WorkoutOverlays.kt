package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.AppColors
import com.gabpawang.app.ui.theme.GreenAccent
import com.gabpawang.app.ui.theme.RedAlert
import com.gabpawang.app.ui.theme.Yellow

/** Pre-workout countdown overlay shown while user gets into push-up position. */
@Composable
internal fun CountdownOverlay(countdownRemaining: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xE6080E18)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.workout_pose_prompt),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$countdownRemaining",
                color = Yellow,
                fontSize = 120.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 130.sp
            )
            Text(
                text = stringResource(R.string.workout_seconds_until_start),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }
    }
}

/** Between-set rest overlay with circular progress + action buttons. */
@Composable
internal fun RestOverlay(
    colors: AppColors,
    restRemaining: Int,
    restTotalSec: Int,
    onSkipRest: () -> Unit,
    onExtendRest: () -> Unit,
    onFinishAll: () -> Unit
) {
    // Intentional hardcoded dark overlay — full-screen cover during rest period over camera feed
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080E18)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.workout_rest_title),
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(220.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress = if (restTotalSec > 0) restRemaining.toFloat() / restTotalSec else 0f
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 14.dp,
                    color = colors.accent,
                    trackColor = colors.bgCard,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${restRemaining}",
                    color = colors.accent,
                    fontSize = 78.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 84.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                stringResource(R.string.workout_seconds_until_next_set),
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(28.dp))
            BtnPrimary(
                text = stringResource(R.string.workout_start_now),
                onClick = onSkipRest,
                color = GreenAccent
            )
            Spacer(modifier = Modifier.height(10.dp))
            BtnPrimary(
                text = stringResource(R.string.workout_extend_rest),
                onClick = onExtendRest,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(10.dp))
            BtnPrimary(
                text = stringResource(R.string.workout_save_and_home),
                onClick = onFinishAll,
                color = RedAlert
            )
        }
    }
}
