package com.gabpawang.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BtnGhost
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.KakaoYellow
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.NaverGreen
import com.gabpawang.app.ui.theme.Yellow

@Composable
fun SignupScreen(
    onNext: () -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onKakaoSignIn: () -> Unit = {}
) {
    val colors = LocalAppColors.current
    var showEmail by remember { mutableStateOf(false) }
    var showGuestSheet by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(colors.bgDark)) {
        Column(
            modifier = Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusBarSpacer()
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.signup_title),
                color = colors.textPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.signup_subtitle),
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(36.dp))

            BrandButton(
                text = stringResource(R.string.signup_kakao),
                bg = KakaoYellow,
                fg = Color.Black
            ) { onKakaoSignIn() }
            Spacer(modifier = Modifier.height(10.dp))
            BrandButton(
                text = stringResource(R.string.signup_google),
                bg = Color.White,
                fg = Color.Black
            ) { onGoogleSignIn() }
            Spacer(modifier = Modifier.height(10.dp))
            BrandButton(
                text = stringResource(R.string.signup_naver),
                bg = NaverGreen,
                fg = Color.White
            ) { onNext() }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (showEmail) {
                    stringResource(R.string.signup_email_hide)
                } else {
                    stringResource(R.string.signup_email_show)
                },
                color = colors.accent,
                fontSize = 14.sp,
                modifier = Modifier.clickable { showEmail = !showEmail }
            )

            if (showEmail) {
                Spacer(modifier = Modifier.height(16.dp))
                EmailField(stringResource(R.string.signup_nickname), nickname) { nickname = it }
                Spacer(modifier = Modifier.height(8.dp))
                EmailField(stringResource(R.string.signup_email), email) { email = it }
                Spacer(modifier = Modifier.height(8.dp))
                EmailField(stringResource(R.string.signup_password), pw) { pw = it }
                Spacer(modifier = Modifier.height(12.dp))
                BtnPrimary(
                    text = stringResource(R.string.signup_submit),
                    onClick = { onNext() }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            BtnGhost(
                text = stringResource(R.string.signup_guest),
                onClick = { showGuestSheet = true }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
        if (showGuestSheet) {
            GuestSheet(
                onCancel = { showGuestSheet = false },
                onContinue = { showGuestSheet = false; onNext() }
            )
        }
    }
}

@Composable
private fun BrandButton(
    text: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bg, contentColor = fg)
    ) {
        Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EmailField(label: String, value: String, onChange: (String) -> Unit) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = colors.textSub, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.bgCard)
                .border(1.dp, colors.borderCard, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(color = colors.textPrimary, fontSize = 14.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(Yellow)
            )
        }
    }
}

@Composable
private fun GuestSheet(onCancel: () -> Unit, onContinue: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onCancel() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                .background(colors.bgSheet)
                .navigationBarsPadding()
                .padding(24.dp)
                .clickable(enabled = false) {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp, 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                stringResource(R.string.guest_sheet_title),
                color = colors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.guest_sheet_body),
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            BtnPrimary(text = stringResource(R.string.common_continue), onClick = onContinue)
            Spacer(modifier = Modifier.height(8.dp))
            BtnGhost(text = stringResource(R.string.common_cancel), onClick = onCancel)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
