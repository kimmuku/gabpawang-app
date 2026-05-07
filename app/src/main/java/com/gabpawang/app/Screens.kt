package com.gabpawang.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.RedAlert
import com.gabpawang.app.ui.theme.Yellow

@Composable
internal fun PermissionScreen(onRequest: () -> Unit) {
    val colors = LocalAppColors.current
    Box(Modifier.fillMaxSize().background(colors.bgDark), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("카메라 권한이 필요합니다", color = colors.textPrimary, fontSize = 18.sp)
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow,
                    contentColor = androidx.compose.ui.graphics.Color.Black
                )
            ) { Text("권한 허용") }
        }
    }
}

@Composable
internal fun StatusScreen(message: String) {
    val colors = LocalAppColors.current
    Box(Modifier.fillMaxSize().background(colors.bgDark), contentAlignment = Alignment.Center) {
        Text(message, color = colors.textPrimary, fontSize = 16.sp)
    }
}

@Composable
internal fun DownloadingScreen(progress: Float) {
    val colors = LocalAppColors.current
    Box(Modifier.fillMaxSize().background(colors.bgDark), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "BlazePose 모델 다운로드 중",
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("처음 실행 시 1회만 (~9 MB)", color = colors.textSub, fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = Yellow,
                trackColor = colors.textSub
            )
            Spacer(Modifier.height(10.dp))
            Text("${(progress * 100).toInt()}%", color = colors.accent, fontSize = 14.sp)
        }
    }
}

@Composable
internal fun ErrorScreen(message: String, onRetry: () -> Unit) {
    val colors = LocalAppColors.current
    Box(Modifier.fillMaxSize().background(colors.bgDark), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("오류 발생", color = RedAlert, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, color = colors.textSub, fontSize = 13.sp)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Yellow,
                    contentColor = androidx.compose.ui.graphics.Color.Black
                )
            ) { Text("다시 시도") }
        }
    }
}
