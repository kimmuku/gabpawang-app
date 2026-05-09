package com.gabpawang.app.feature.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.WorkoutConfig
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.theme.LocalAppColors

fun displayMode(mode: String): String = when (mode) {
    "free" -> "자유"
    "target" -> "목표"
    "timed" -> "타임어택"
    "challenge" -> "챌린지 100"
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
                text = "카메라 준비 중",
                color = colors.textPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "푸쉬업 자세를 잡고 화면에 어깨가 보이게 해주세요.",
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "모드: ${displayMode(config.mode)}",
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
            BtnPrimary(text = "운동 시작하기 💪", onClick = onStart)
        }
    }
}
