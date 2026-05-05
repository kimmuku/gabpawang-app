# 03. 디자인 토큰

프로토타입 `갑빠왕_prototype_v2.html`에서 추출한 토큰입니다.
Compose의 `MaterialTheme`과 커스텀 토큰 객체로 양분합니다.

## 색상 (Color.kt)

```kotlin
package com.gabpaking.core.ui.theme

import androidx.compose.ui.graphics.Color

object GabpaColors {
    // Brand
    val Yellow       = Color(0xFFFFC400)  // primary
    val Orange       = Color(0xFFFF8C00)  // primary gradient end
    val Red          = Color(0xFFFF5500)  // emphasis
    val Gold         = Color(0xFFFFDD00)  // particle/accent

    // Background (dark)
    val BgBase       = Color(0xFF0A0A12)  // 앱 배경
    val BgSurface    = Color(0xFF1A1A26)  // 카드, 시트
    val BgElevated   = Color(0xFF22222F)  // 더 위 카드
    val BgRadial     = Color(0xFF0A0A12)  // 홈 캐릭터 후광 베이스

    // Text
    val TextPrimary  = Color(0xFFFFFFFF)
    val TextSecondary= Color(0x99FFFFFF) // 60%
    val TextMuted    = Color(0x66FFFFFF) // 40%
    val TextDisabled = Color(0x33FFFFFF) // 20%

    // Borders / dividers
    val Border       = Color(0x14FFFFFF) // 8%
    val BorderStrong = Color(0x1FFFFFFF) // 12%
    val Divider      = Color(0x0DFFFFFF) // 5%

    // States
    val Success      = Color(0xFF00C853)
    val Warning      = Color(0xFFFFC400)
    val Error        = Color(0xFFFF5252)

    // Kakao
    val Kakao        = Color(0xFFFEE500)
    val KakaoText    = Color(0xFF3C1E1E)

    // Heatmap (캘린더)
    val Heat1        = Color(0x26FFC400) // 15%
    val Heat2        = Color(0x4DFFC400) // 30%
    val Heat3        = Color(0x8CFFC400) // 55%
    val Heat4        = Color(0xD9FFC400) // 85%
}
```

## 타이포그래피 (Type.kt)

폰트는 Pretendard 권장 (한글 가독성 + 갑빠왕의 모던한 느낌).
`fonts/PretendardVariable.ttf`를 res/font/ 에 추가.

```kotlin
val Pretendard = FontFamily(
    Font(R.font.pretendard_variable, FontWeight.Normal),
    Font(R.font.pretendard_variable, FontWeight.Medium),
    Font(R.font.pretendard_variable, FontWeight.SemiBold),
    Font(R.font.pretendard_variable, FontWeight.Bold),
    Font(R.font.pretendard_variable, FontWeight.ExtraBold),
    Font(R.font.pretendard_variable, FontWeight.Black),
)

val GabpaTypography = Typography(
    displayLarge = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Black, fontSize=32.sp, letterSpacing=(-0.03).em),
    displayMedium= TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Black, fontSize=28.sp, letterSpacing=(-0.03).em),
    headlineLarge= TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Black, fontSize=24.sp, letterSpacing=(-0.03).em),
    headlineMedium=TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.ExtraBold, fontSize=20.sp),
    titleLarge   = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.ExtraBold, fontSize=18.sp),
    titleMedium  = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Bold, fontSize=16.sp),
    titleSmall   = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Bold, fontSize=14.sp),
    bodyLarge    = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Normal, fontSize=15.sp, lineHeight=22.sp),
    bodyMedium   = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Normal, fontSize=13.sp, lineHeight=20.sp),
    labelLarge   = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.Bold, fontSize=14.sp, letterSpacing=0.05.em),
    labelMedium  = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.SemiBold, fontSize=12.sp),
    labelSmall   = TextStyle(fontFamily=Pretendard, fontWeight=FontWeight.SemiBold, fontSize=10.sp, letterSpacing=0.18.em),
)
```

## 간격 / 라운딩 / 그림자

```kotlin
object GabpaSpacing {
    val xs = 4.dp; val sm = 8.dp; val md = 12.dp
    val lg = 16.dp; val xl = 20.dp; val xxl = 28.dp
    val screenH = 20.dp   // 화면 좌우 패딩
    val cardP = 16.dp     // 카드 내부 패딩
}

object GabpaShape {
    val small = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(14.dp)
    val large = RoundedCornerShape(18.dp)
    val xl = RoundedCornerShape(24.dp)
    val pill = RoundedCornerShape(999.dp)
    val sheet = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
}
```

## 공통 컴포넌트

프로토타입의 컴포넌트 → Compose 매핑:

| 프로토타입 | Compose 컴포저블 |
|---|---|
| `<BtnPrimary>` | `GabpaPrimaryButton` (옐로우 그라디언트, 50dp 높이) |
| `<BtnGhost>` | `GabpaGhostButton` (투명 + 옅은 보더) |
| `<ProgressBar>` | `GabpaProgressBar(value, max, extra)` |
| `<GabpaChar stage size>` | `GabpaCharacter(stage, size)` — 단계별 SVG asset |
| `<StatusBar>` | 시스템 상태바 (Edge-to-edge로 처리, 별도 컴포넌트 X) |
| `<HomeIndicator>` | 시스템 제스처 영역 (Compose에서는 `WindowInsets.systemBars`) |
| `<BottomNav>` | `NavigationBar` (Material3) — 홈/기록/챌린지/캐릭터/설정 |

## 캐릭터 에셋

9단계 캐릭터 일러스트가 필요합니다. 프로토타입의 `GabpaChar`는 SVG로 그렸지만,
실제 앱에서는 **단계당 1장 PNG (또는 Lottie 애니메이션)**으로 준비.

- 위치: `app/src/main/res/drawable/char_stage_1.png` ~ `char_stage_9.png`
- 권장 크기: 512x512, 투명 배경
- 레벨업 화면용 애니메이션은 **Lottie**로 별도 (`char_stage_N_levelup.json`)

디자이너 작업 전이라면 프로토타입의 SVG를 임시로 임베드해서 진행 가능.
