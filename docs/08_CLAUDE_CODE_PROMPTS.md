# 08. Claude Code 프롬프트 모음

이 문서의 프롬프트는 **그대로 복사 → Claude Code에 붙여넣기** 하면 됩니다.
앞 문서들이 있다는 가정이고, Claude Code가 핸드오프 폴더와 프로토타입을 읽도록 지시합니다.

## 0. 첫 시작 (프로젝트 초기 진입)

```
이 프로젝트의 모든 핸드오프 문서를 읽어줘.
@handoff/README.md @handoff/01_PROJECT_SPEC.md @handoff/02_TECH_STACK.md
@handoff/03_DESIGN_TOKENS.md @handoff/04_DATA_MODEL.md @handoff/05_SCREENS.md
@handoff/06_PUSHUP_COUNTER.md @handoff/07_MILESTONES.md

그리고 프로토타입도 봐줘 — 이게 시각적 단일 진실 공급원이야:
@갑빠왕_prototype_v2.html

다 읽었으면 이 프로젝트가 무엇인지, 다음 1주(Sprint 1)에 뭘 만들어야 하는지
한 단락으로 요약해줘. 코딩은 아직 시작하지 마.
```

## Sprint 1 — 프로젝트 부트스트랩

### 1.1 Android 프로젝트 생성

```
Sprint 1을 시작하자. @handoff/02_TECH_STACK.md 의 스택대로 Android 프로젝트를
이 폴더에 만들어줘:
- 패키지: com.gabpaking
- minSdk 26, targetSdk 35, Kotlin 1.9, Compose BOM 2024.06
- libs.versions.toml로 버전 카탈로그 사용
- Hilt, Navigation Compose, Room, CameraX, Coil, Vico, Supabase Kotlin Client 의존성 추가
- 패키지 구조는 02_TECH_STACK.md 의 트리대로

만든 후 빌드 명령어를 알려줘.
```

### 1.2 디자인 토큰

```
@handoff/03_DESIGN_TOKENS.md 대로 다음 파일을 만들어줘:
- core/ui/theme/Color.kt
- core/ui/theme/Type.kt
- core/ui/theme/Shape.kt
- core/ui/theme/Theme.kt (GabpaTheme composable, 다크 모드 강제)
- core/ui/theme/Spacing.kt

그리고 res/font/pretendard_variable.ttf 자리만 만들어 두고
폰트가 없으면 일단 SansSerif로 빠지게 fallback 처리.
```

### 1.3 공통 컴포넌트

```
프로토타입의 공통 컴포넌트를 Compose로 옮겨줘:
- BtnPrimary → GabpaPrimaryButton (옐로우 그라디언트, 50dp)
- BtnGhost → GabpaGhostButton
- ProgressBar → GabpaProgressBar(value, max, extra=null)
- BottomNav → GabpaBottomBar (홈/기록/챌린지/캐릭터/설정 5탭)
- StatusBar/HomeIndicator는 Compose에서는 WindowInsets로 처리하니 만들 필요 X

GabpaCharacter(stage: Int, size: Dp, glow: Boolean=false)는 일단
res/drawable/char_stage_1..9.png 가 없으니 placeholder Box(노란 동그라미 + "S{stage}")로.
@handoff/03_DESIGN_TOKENS.md 의 토큰만 사용.
```

### 1.4 Supabase 셋업

```
Supabase Kotlin Client 초기화:
- core/data/remote/SupabaseClient.kt
- BuildConfig 에 SUPABASE_URL, SUPABASE_ANON_KEY 주입 (gradle.properties → BuildConfig field)
- Hilt 모듈 (di/NetworkModule.kt) 에서 SupabaseClient 싱글톤 제공

다음 명령으로 supabase 프로젝트 만들 거니까, 어떤 정보가 필요한지 알려줘:
1) 프로젝트 URL, anon key
2) 04_DATA_MODEL.md 의 SQL 실행 (내가 직접 콘솔에서 할 거야)
```

### 1.5 인증 흐름

```
@handoff/05_SCREENS.md 의 라우트 트리대로 NavGraph 만들고,
Splash → Onboarding/Intro → Login → GoalSetup → Home(empty) 흐름 구현.

- 구글 로그인은 supabase auth.signInWith(Google)
- 이메일은 supabase auth.signInWith(email, password) + 회원가입
- 카카오는 일단 버튼만 두고 onClick = TODO()
- GoalSetup에서 닉네임/목표 받아서 profiles 테이블에 INSERT
- Home은 일단 "안녕 {nickname}" 만 보여주고 끝

Splash에서 세션 복원 → home 으로 자동 진입까지.
```

### 1.6 카카오 OAuth (Sprint 1 마지막)

```
카카오 로그인을 Supabase Edge Function 으로 연결해줘.
- KakaoSDK 초기화 (AndroidManifest 에 카카오 앱 키 메타데이터)
- UserApiClient.loginWithKakaoTalk → idToken 획득
- Supabase Edge Function 'kakao-auth' 에 POST {id_token}
  - 함수가 카카오 토큰 검증 → 카카오 ID로 supabase.auth.admin.createUser() 또는 기존 유저 조회
  - JWT 반환
- 받은 JWT 로 supabase.auth.setSession()

Edge Function 코드도 supabase/functions/kakao-auth/index.ts 에 만들어줘.
```

## Sprint 2 — 운동 코어

```
Sprint 2 시작. @handoff/05_SCREENS.md 의 Workout Start / Running / Result 와
@handoff/04_DATA_MODEL.md 의 workout_sessions, workout_sets 를 사용해서:

1. WorkoutStartScreen (모드 3개 선택)
2. WorkoutRunningScreen (지금은 가짜 카운터)
   - FakePushupCounter (06_PUSHUP_COUNTER.md 마지막 섹션) 사용
   - 1.5초마다 카운트 +1 시뮬
   - 세트 휴식 60초 타이머, 스킵 가능
3. WorkoutResultScreen (총횟수, 세트별, 진화 진행도)
4. workout_sessions/sets INSERT, ended_at 갱신으로 트리거 발동
5. 홈으로 돌아오면 누적/1회최고/연속이 실시간 반영

Repository 패턴 + Room 캐싱 + 오프라인 시 Outbox 큐.
```

## Sprint 3 — 비전 카운터 & 기록

```
Sprint 3 시작. 두 가지를 진행:

A. 비전 카운터 통합
   - 내가 가진 모델은 [여기에 모델 형식 적기: TFLite/ML Kit/기타]
   - PushupCounter 인터페이스 (06_PUSHUP_COUNTER.md) 구현
   - WorkoutRunningScreen 에 CameraX PreviewView + ImageAnalysis 연결
   - 카메라 권한 흐름 + 폴백 +1 버튼

B. Record 화면 3탭 (1rm / calendar / stats)
   - Calendar 는 daily_summary 에서 월 단위 fetch
   - 히트맵 셀 디자인은 프로토타입의 개선된 형태 (날짜 좌상단, 횟수 중앙 + "회")
   - Stats 는 Vico 차트로 월별 1회 최고 막대그래프
```

## Sprint 4 — 캐릭터, 레벨업, 마무리

```
Sprint 4 시작:

1. CharacterScreen (진화 로드맵 가로 스크롤)
2. LevelUpScreen
   - Lottie 애니메이션 자리 (없으면 캐릭터 + 파티클)
   - 카카오 공유 바텀시트 (미리보기 카드 + 카카오톡으로 보내기 + 이미지 저장)
   - KakaoLinkClient.shareDefault 로 FeedTemplate 전송
3. PrScreen (1회 최고 갱신)
4. NotificationsScreen (notifications 테이블 조회)
5. SettingsScreen
6. Crashlytics + Analytics 연동
7. 내부 테스트 트랙 빌드/배포
```

## 추가 작업 프롬프트

### 디자인 검수
```
지금 만든 [화면명]을 프로토타입의 같은 화면(@갑빠왕_prototype_v2.html)과
비교해서 어긋난 부분을 5개만 짚어줘. 색/간격/크기/타이포 위주로.
```

### 버그 디버깅
```
[증상 설명]. 관련 파일을 찾고 원인 가설 3개 제시 → 그 중 가장 가능성 높은 1개를
검증할 수 있는 로그 추가 → 어떤 입력으로 재현하면 되는지 알려줘.
```

### 리팩토링
```
@feature/workout/ 전체를 보고 다음을 점검해줘:
- ViewModel 이 너무 많은 책임 갖고 있지 않은지
- Repository 가 직접 supabase 호출하는 곳이 남아있는지
- StateFlow 노출이 일관적인지
개선안 3개 + 우선순위.
```

## 중요한 약속들 (Claude Code에게 항상 상기시키기)

```
규칙:
1. 프로토타입(@갑빠왕_prototype_v2.html)이 시각적 단일 진실 공급원이다.
   문서와 충돌 시 프로토타입 우선.
2. stageBoundaries = [0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000]
   는 절대 바꾸지 않는다.
3. 카메라 영상은 절대 네트워크로 전송 금지. 온디바이스 only.
4. 친구/소셜 기능은 MVP에 없다. 임의로 추가하지 마.
5. 모든 한국어 텍스트는 프로토타입의 톤(직설, 짧음, 운동 친화적)을 유지.
   "당신의 갑빠가 진화했어요" O / "축하드립니다 회원님" X
6. 1회 = "1회", 횟수에는 "회" 단위 명시.
```
