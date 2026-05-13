package com.gabpawang.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.BuildConfig
import com.gabpawang.app.R
import com.gabpawang.app.data.remote.FeedbackRepository
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.RedAlert
import com.gabpawang.app.ui.theme.Yellow
import kotlinx.coroutines.launch

private const val MAX_LEN = 500

@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()
    val failureMessage = stringResource(R.string.feedback_failed)

    var text by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var done by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
            StatusBarSpacer()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "←",
                    color = colors.textPrimary,
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    stringResource(R.string.feedback_title),
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (done) {
                DoneContent(onBack)
            } else {
                InputContent(
                    text = text,
                    onTextChange = { if (it.length <= MAX_LEN) text = it },
                    submitting = submitting,
                    errorMsg = errorMsg,
                    onSubmit = {
                        scope.launch {
                            submitting = true
                            errorMsg = null
                            val result = FeedbackRepository.submit(
                                content = text.trim(),
                                appVersion = BuildConfig.VERSION_NAME,
                                deviceModel = android.os.Build.MODEL
                            )
                            submitting = false
                            if (result.isSuccess) done = true
                            else errorMsg = failureMessage
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DoneContent(onBack: () -> Unit) {
    val colors = LocalAppColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text("✅", fontSize = 56.sp)
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(R.string.feedback_done_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Yellow
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(R.string.feedback_done_subtitle),
                fontSize = 14.sp,
                color = colors.textSub
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Yellow, contentColor = Color.Black)
            ) { Text(stringResource(R.string.common_confirm), fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun InputContent(
    text: String,
    onTextChange: (String) -> Unit,
    submitting: Boolean,
    errorMsg: String?,
    onSubmit: () -> Unit
) {
    val colors = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.feedback_prompt), color = colors.textSub, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = {
                Text(
                    stringResource(R.string.feedback_placeholder),
                    color = colors.textSub,
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Yellow,
                unfocusedBorderColor = colors.bgCard,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                cursorColor = Yellow
            ),
            maxLines = 10
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(
                stringResource(R.string.feedback_length_format, text.length, MAX_LEN),
                color = colors.textSub,
                fontSize = 11.sp
            )
        }
        errorMsg?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = RedAlert, fontSize = 12.sp)
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onSubmit,
            enabled = text.trim().isNotEmpty() && !submitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Yellow, contentColor = Color.Black)
        ) {
            if (submitting) {
                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.feedback_send), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
