package com.gabpawang.app.feature.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gabpawang.app.R
import com.gabpawang.app.ui.components.BtnGhost
import com.gabpawang.app.ui.components.BtnPrimary
import com.gabpawang.app.ui.components.GabpaChar
import com.gabpawang.app.ui.components.StatusBarSpacer
import com.gabpawang.app.ui.theme.KakaoYellow
import com.gabpawang.app.ui.theme.LocalAppColors
import com.gabpawang.app.ui.theme.NaverGreen
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

private data class Slide(val stage: Int, val title: String, val desc: String)

private val slides = listOf(
    Slide(2, "푸쉬업으로 진화하세요", "AI가 자세를 분석하고 푸쉬업을 자동으로 카운트합니다."),
    Slide(5, "9단계 캐릭터 진화", "마른 체형부터 갑빠왕까지, 운동량에 따라 진화합니다."),
    Slide(8, "친구와 함께 도전", "챌린지에 참여하고 친구와 기록을 공유하세요.")
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
                    text = "건너뛰기",
                    color = colors.textSub,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNext() }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            GabpaChar(stage = slide.stage, sizeDp = 140.dp, glow = true)
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = slide.title,
                color = colors.textPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = slide.desc,
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
                text = if (page == slides.lastIndex) "시작하기" else "다음",
                onClick = {
                    if (page == slides.lastIndex) onNext()
                    else page++
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

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
                text = "계정 만들기",
                color = colors.textPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "기록을 안전하게 보관하세요",
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(36.dp))

            BrandButton("카카오로 시작하기", KakaoYellow, Color.Black) { onKakaoSignIn() }
            Spacer(modifier = Modifier.height(10.dp))
            BrandButton("Google로 시작하기", Color.White, Color.Black) { onGoogleSignIn() }
            Spacer(modifier = Modifier.height(10.dp))
            BrandButton("네이버로 시작하기", NaverGreen, Color.White) { onNext() }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (showEmail) "닫기" else "이메일로 가입",
                color = colors.accent,
                fontSize = 14.sp,
                modifier = Modifier.clickable { showEmail = !showEmail }
            )

            if (showEmail) {
                Spacer(modifier = Modifier.height(16.dp))
                EmailField("닉네임", nickname) { nickname = it }
                Spacer(modifier = Modifier.height(8.dp))
                EmailField("이메일", email) { email = it }
                Spacer(modifier = Modifier.height(8.dp))
                EmailField("비밀번호", pw) { pw = it }
                Spacer(modifier = Modifier.height(12.dp))
                BtnPrimary(text = "가입하기", onClick = { onNext() })
            }

            Spacer(modifier = Modifier.weight(1f))
            BtnGhost(text = "게스트로 시작하기", onClick = { showGuestSheet = true })
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
            Text("게스트 모드 안내", color = colors.textPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "기록은 이 기기에만 저장됩니다. 앱을 삭제하거나 기기를 변경하면 데이터가 사라질 수 있어요.",
                color = colors.textSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            BtnPrimary(text = "계속하기", onClick = onContinue)
            Spacer(modifier = Modifier.height(8.dp))
            BtnGhost(text = "취소", onClick = onCancel)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
