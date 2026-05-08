# 갑빠왕 iOS

안드로이드(Kotlin/Compose) 버전의 SwiftUI 네이티브 포팅입니다.

## 요구사항

- macOS + Xcode 15.4+
- iOS 17.0+ (SwiftData 사용)
- [XcodeGen](https://github.com/yonaskolb/XcodeGen) (`brew install xcodegen`) — `.xcodeproj`를 생성하기 위해 사용

## 처음 셋업

```bash
cd ios
xcodegen generate          # project.yml → GabpaWang.xcodeproj 생성
open GabpaWang.xcodeproj
```

Xcode가 열리면 자동으로 SPM 패키지(Supabase, GoogleSignIn, Kakao SDK)를 받습니다.
시뮬레이터 또는 실제 기기에서 빌드해 실행하세요.

## 폴더 구조 (Android와의 매핑)

```
ios/GabpaWang/
├── App/                     ← MainActivity / GabpaApplication / AppState / AppViewModel
│   ├── GabpaWangApp.swift           (진입점, @main)
│   ├── AppDelegate.swift            (Kakao/Google SDK 초기화)
│   ├── AppConfig.swift              (Supabase URL, Kakao key, Google client id)
│   ├── AppState.swift               (네비게이션 + 일시 상태) ← AppState.kt
│   ├── AppViewModel.swift           (DB-backed 누적/단계/연속) ← AppViewModel.kt
│   ├── StageThresholds.swift        (단계 임계값/이름/순위)  ← AppState.kt 후반
│   └── RootView.swift               (라우터)              ← AppRouter
├── UI/
│   ├── Theme/
│   │   ├── Colors.swift             ← Color.kt (다크/라이트 두 셋)
│   │   ├── Typography.swift         ← Type.kt
│   │   └── RoundedShapes.swift
│   └── Components/
│       ├── PrimaryButton.swift      ← BtnPrimary
│       ├── GabpaProgressBar.swift   ← GabpaProgressBar
│       ├── BackHeader.swift         ← BackHeader / SectionTitle
│       ├── StatCard.swift           ← StatCard
│       ├── BottomNav.swift          ← BottomNav
│       └── GabpaCharacter.swift     ← GabpaChar (asset 없을 시 SF Symbol 폴백)
├── Features/
│   ├── Onboarding/TutorialView.swift          ← TutorialScreen.kt
│   ├── Home/HomeView.swift                    ← HomeScreen.kt
│   ├── Workout/
│   │   ├── WorkoutStartView.swift             ← WorkoutStartScreen.kt
│   │   ├── WorkoutRunningView.swift           ← WorkoutRunningScreen.kt
│   │   ├── WorkoutResultView.swift            ← WorkoutResultScreen.kt
│   │   ├── LevelUpView.swift                  ← LevelUpScreen.kt
│   │   ├── WorkoutViewModel.swift             ← MainViewModel.kt
│   │   ├── CameraPreview.swift                ← CameraBackground.kt
│   │   └── PoseSkeletonOverlay.swift          ← SkeletonOverlay.kt
│   ├── Record/RecordView.swift                ← RecordScreen.kt + RecordViewModel.kt + CalendarTab.kt
│   ├── Character/CharacterView.swift          ← CharacterScreen.kt
│   ├── Notifications/NotificationsView.swift  ← NotificationScreen.kt
│   └── Settings/SettingsView.swift            ← SettingsScreen.kt
├── Data/
│   ├── Local/
│   │   ├── WorkoutSessionEntity.swift   (SwiftData @Model) ← Room WorkoutSessionEntity.kt
│   │   └── WorkoutRepository.swift                          ← WorkoutRepository.kt
│   ├── Remote/
│   │   ├── SupabaseClient.swift                             ← SupabaseClientProvider.kt + DTOs
│   │   ├── SupabaseSync.swift                               ← SupabaseSync.kt
│   │   └── AuthRepository.swift                             ← AuthRepository.kt
│   └── Pushup/
│       ├── PushUpCounter.swift          (순수 알고리즘 1:1 포팅) ← PushUpCounter.kt
│       ├── PoseLandmarkerHelper.swift   (Apple Vision 래퍼)    ← PoseLandmarkerHelper.kt
│       └── CameraSession.swift          (AVCaptureSession 래퍼)
└── Resources/
    ├── Info.plist
    ├── Assets.xcassets/                 (앱 아이콘, 캐릭터 9단계 PNG 추가 필요)
    └── Fonts/                           (Pretendard 4종 .otf 추가 필요)
```

## 안드로이드 → iOS 라이브러리 매핑

| 안드로이드 | iOS |
|---|---|
| Jetpack Compose | SwiftUI |
| CameraX | AVFoundation (`AVCaptureSession`) |
| MediaPipe BlazePose | Apple **Vision** (`VNDetectHumanBodyPoseRequest`) — 모델 다운로드 불필요 |
| Room | **SwiftData** (`@Model`, iOS 17+) |
| Supabase Kotlin SDK | [supabase-swift](https://github.com/supabase/supabase-swift) |
| Kakao SDK (Android) | [kakao-ios-sdk-rx](https://github.com/kakao/kakao-ios-sdk-rx) |
| Google Credentials Manager | [GoogleSignIn-iOS](https://github.com/google/GoogleSignIn-iOS) |
| Coroutine `StateFlow` | `@Published` + `ObservableObject` |
| `SharedPreferences` | `UserDefaults` |
| `BackHandler` | `AppState` 백 스택 (수동 관리, 안드로이드 동일 모델) |

## 진행 현황

✅ **완료**
- 캐릭터 일러스트 10단계(char_stage_1~10) `Assets.xcassets`에 추가
- Pretendard 폰트 4종(.ttf) `Resources/Fonts/`에 추가
- pushup_guide.png 추가
- 음성 카운트 (`AVSpeechSynthesizer` + `SpeechCounter.swift`)
- 챌린지 화면 stub (`ChallengeView.swift`) + Screen.challenge 라우트
- 카카오 OAuth Edge Function 초안 (`supabase/functions/kakao-auth/`) +
  iOS `AuthRepository.signInWithKakao`에 호출 흐름 연결

⚠️ **남은 일**
1. **카카오 Edge Function 배포** — `supabase functions deploy kakao-auth`로 업로드 필요. Supabase Swift SDK의 `verifyOTP` 시그니처는 SDK 버전에 따라 미세하게 다를 수 있어 실 빌드 시 한 번 확인.
2. **알림 / 리마인더** — 현재 `NotificationsView`는 더미 데이터. `UserNotifications` 프레임워크로 로컬/원격 알림 연동 필요.
3. **Vision 키포인트 매핑 실기기 검증** — 시뮬레이터에서는 카메라/Vision이 제한적. 실 디바이스에서 어깨 Y 좌표가 알고리즘이 기대하는 분포(0~1)에 맞는지 한 번 검증.
4. **앱 아이콘 1024×1024** — `AppIcon.appiconset`은 자리만 잡혀 있음. 실제 PNG 추가 필요.
5. **첫 빌드 시 컴파일 에러** — Mac/Xcode가 없는 환경에서 작성된 코드라 SPM 패키지 API 시그니처 미세 차이로 사소한 수정이 필요할 수 있음.

## 카메라/포즈 검출 노트

iOS의 `VNDetectHumanBodyPoseRequest`는 BlazePose와 키포인트 인덱스가 다릅니다.
하지만 푸시업 카운터(`PushUpCounter.swift`)가 필요한 입력은 단 하나 — **어깨 Y 좌표**.
`PoseLandmarkerHelper.PoseFrame.midShoulderY`로 추상화되어 있어, 알고리즘 자체는
안드로이드 코드와 1바이트 단위로 동일합니다.

Vision 좌표계는 좌하단 원점이라 `1 - y`로 뒤집어 안드로이드의 좌상단 원점과
같게 맞췄습니다.

## 빌드 인증서

소셜 로그인 동작을 위해 Info.plist의 URL Scheme이 다음과 일치해야 합니다.

- 카카오: `kakao{NATIVE_APP_KEY}` — 이미 `73af7802f9ee24065925157c33d9e031`로 채워둠.
- 구글: `com.googleusercontent.apps.{REVERSED_CLIENT_ID}` — `AppConfig.googleClientID`와 동일한 reverse를 사용.

값이 달라지면 Kakao Developers / Google Cloud Console에서 `iOS 번들 ID`를
`com.gabpawang.app`으로 등록한 뒤 새 키로 교체하세요.
