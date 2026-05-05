package com.example.pushupcounter.feature.onboarding

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushupcounter.ui.components.BtnGhost
import com.example.pushupcounter.ui.components.BtnPrimary
import com.example.pushupcounter.ui.components.GabpaChar
import com.example.pushupcounter.ui.components.StatusBarSpacer
import com.example.pushupcounter.ui.theme.BgCard
import com.example.pushupcounter.ui.theme.BgDark
import com.example.pushupcounter.ui.theme.BgSheet
import com.example.pushupcounter.ui.theme.BorderCard
import com.example.pushupcounter.ui.theme.KakaoYellow
import com.example.pushupcounter.ui.theme.NaverGreen
import com.example.pushupcounter.ui.theme.TextPrimary
import com.example.pushupcounter.ui.theme.TextSub
import com.example.pushupcounter.ui.theme.Yellow
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNext: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onNext()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0x2EFFC400), Color(0x00FFC400), BgDark),
                    radius = 800f
                )
            )
            .background(BgDark.copy(alpha = 0.0f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            GabpaChar(stage = 7, sizeDp = 100.dp, glow = true)
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "갑빠왕",
                color = Yellow,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "푸쉬업으로 단계 진화",
                color = TextSub,
                fontSize = 14.sp
            )
        }
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
    var page by remember { mutableStateOf(0) }
    val slide = slides[page]

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
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
                    color = TextSub,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onNext() }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            GabpaChar(stage = slide.stage, sizeDp = 140.dp, glow = true)
            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = slide.title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = slide.desc,
                color = TextSub,
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
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SignupScreen(onNext: () -> Unit) {
    var showEmail by remember { mutableStateOf(false) }
    var showGuestSheet by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pw by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(BgDark)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusBarSpacer()
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "계정 만들기",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "기록을 안전하게 보관하세요",
                color = TextSub,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(36.dp))

            BrandButton("카카오로 시작하기", KakaoYellow, Color.Black) { onNext() }
            Spacer(modifier = Modifier.height(10.dp))
            BrandButton("Google로 시작하기", Color.White, Color.Black) { onNext() }
            Spacer(modifier = Modifier.height(10.dp))
            BrandButton("네이버로 시작하기", NaverGreen, Color.White) { onNext() }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (showEmail) "닫기" else "이메일로 가입",
                color = Yellow,
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
            Spacer(modifier = Modifier.height(40.dp))
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, color = TextSub, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BgCard)
                .border(1.dp, BorderCard, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                textStyle = TextStyle(color = TextPrimary, fontSize = 14.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(Yellow)
            )
        }
    }
}

@Composable
private fun GuestSheet(onCancel: () -> Unit, onContinue: () -> Unit) {
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
                .background(BgSheet)
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
            Text("게스트 모드 안내", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "기록은 이 기기에만 저장됩니다. 앱을 삭제하거나 기기를 변경하면 데이터가 사라질 수 있어요.",
                color = TextSub,
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
