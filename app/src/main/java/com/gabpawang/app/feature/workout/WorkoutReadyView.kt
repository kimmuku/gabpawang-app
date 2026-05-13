package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.LocalAppColors

@Composable
fun displayMode(mode: String): String = when (mode) {
    "free" -> stringResource(R.string.workout_mode_free)
    "target" -> stringResource(R.string.workout_mode_target)
    "timed" -> stringResource(R.string.workout_mode_timed)
    "challenge" -> stringResource(R.string.workout_mode_challenge)
    else -> mode
}

@Composable
fun ReadyView(config: WorkoutConfig, onStart: () -> Unit) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.ready_title),
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.ready_subtitle),
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.ready_mode_format, displayMode(config.mode)),
                color = colors.accent,
                fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                .background(colors.bgCard)
                .border(1.dp, colors.borderCard, androidx.compose.foundation.shape.RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 80.sp)
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            BtnPrimary(text = stringResource(R.string.ready_start_button), onClick = onStart)
        }
    }
}
