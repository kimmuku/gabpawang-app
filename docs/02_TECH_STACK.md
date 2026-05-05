# 02. 기술 스택

## 결정 사항

| 영역 | 선택 | 이유 |
|---|---|---|
| 언어 | Kotlin 1.9+ | 안드로이드 표준 |
| UI | Jetpack Compose + Material3 | 캐릭터/애니메이션이 많은 앱에 적합 |
| 최소 SDK | API 26 (Android 8.0) | 카메라 API 안정성 |
| 타깃 SDK | 최신 (API 35) | Play Store 정책 대응 |
| 빌드 시스템 | Gradle (Kotlin DSL) | 표준 |
| DI | Hilt | 표준, 테스트 용이 |
| 네비게이션 | Navigation Compose | 타입 안전 라우트 |
| 상태 관리 | ViewModel + StateFlow | Compose 친화적 |
| 로컬 DB | Room | Supabase 동기화 전 캐시 |
| 네트워크 | Supabase Kotlin Client | 백엔드 표준 |
| 이미지 | Coil 3 | Compose 친화적 |
| 차트 | Vico (Compose 차트 라이브러리) | 통계 화면용 |
| 카메라 | CameraX | 가장 안정적인 표준 |
| 비전 모델 | TFLite 또는 ML Kit (사용자 보유 모델 통합) | `06_PUSHUP_COUNTER.md` 참조 |
| 결제 | Google Play Billing v7 | 프리미엄 구독 |
| 분석 | Firebase Analytics + Crashlytics | 무료, 풍부한 대시보드 |

## 백엔드: Supabase

| 구성요소 | 용도 |
|---|---|
| Postgres | 운동 기록, 사용자, 캐릭터 상태, 챌린지 |
| Auth | 카카오 + 구글 + 이메일 (OAuth로 통합) |
| Storage | 프로필 사진, 카카오 공유 카드 이미지 |
| Edge Functions | 카카오 OAuth 콜백, 알림 트리거 |
| Realtime | (선택) 친구 기능 추가 시 사용 |

### Supabase 셋업 순서
1. supabase.com에서 프로젝트 생성 (서울 리전 선택)
2. `04_DATA_MODEL.md`의 SQL 실행 (테이블 + RLS)
3. Auth → Providers에서 Google 활성화
4. 카카오는 Supabase가 직접 지원하지 않으니 Edge Function으로 처리 (자세한 건 `08_CLAUDE_CODE_PROMPTS.md` 참조)
5. Storage 버킷: `avatars` (public-read), `share-cards` (signed URL)

## 프로젝트 구조

```
app/
  src/main/java/com/gabpaking/
    core/
      data/
        local/        # Room DB
        remote/       # Supabase Client
        repository/   # Repository 인터페이스 + 구현
      di/             # Hilt 모듈
      ui/
        theme/        # Color, Typography, Shapes
        components/   # GabpaChar, ProgressBar, BtnPrimary 등 공통
    feature/
      auth/           # 로그인, 회원가입
      onboarding/     # 닉네임, 목표 설정
      home/           # 홈 화면 + ViewModel
      workout/
        start/        # 모드 선택
        running/      # 카운팅 진행
        result/       # 결과
      record/         # 1회최고/캘린더/통계
      character/      # 캐릭터 + 진화 로드맵
      challenge/      # 챌린지
      notification/   # 알림 목록
      settings/       # 설정
      levelup/        # 레벨업 + 카카오 공유
    pushup/           # 비전 카운팅 모듈 (사용자 모델)
      detector/
      counter/
    MainActivity.kt
    GabpaApp.kt       # @HiltAndroidApp
```

## Gradle 의존성 (참고)

```kotlin
// libs.versions.toml에 정리
[versions]
kotlin = "1.9.22"
compose-bom = "2024.06.00"
hilt = "2.51"
room = "2.6.1"
camerax = "1.3.4"
supabase = "2.5.0"
coil = "3.0.0"
ktor = "2.3.12"

[libraries]
# Compose BOM
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose-bom" }
compose-ui = { group = "androidx.compose.ui", name = "ui" }
compose-material3 = { group = "androidx.compose.material3", name = "material3" }

# Hilt
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }

# Room
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }

# CameraX
camera-core = { group = "androidx.camera", name = "camera-core", version.ref = "camerax" }
camera-camera2 = { group = "androidx.camera", name = "camera-camera2", version.ref = "camerax" }
camera-lifecycle = { group = "androidx.camera", name = "camera-lifecycle", version.ref = "camerax" }
camera-view = { group = "androidx.camera", name = "camera-view", version.ref = "camerax" }

# Supabase
supabase-bom = { group = "io.github.jan-tennert.supabase", name = "bom", version.ref = "supabase" }
supabase-postgrest = { group = "io.github.jan-tennert.supabase", name = "postgrest-kt" }
supabase-auth = { group = "io.github.jan-tennert.supabase", name = "auth-kt" }
supabase-storage = { group = "io.github.jan-tennert.supabase", name = "storage-kt" }
supabase-realtime = { group = "io.github.jan-tennert.supabase", name = "realtime-kt" }
ktor-engine = { group = "io.ktor", name = "ktor-client-android", version.ref = "ktor" }

# 카카오 SDK
kakao-user = { group = "com.kakao.sdk", name = "v2-user", version = "2.20.6" }
kakao-share = { group = "com.kakao.sdk", name = "v2-share", version = "2.20.6" }
```

## 빌드 변형 (Build Variants)

- `debug`: Supabase dev 프로젝트, 로깅 풀, 가짜 비전 모델 사용 가능
- `release`: Supabase prod, R8 최적화, ProGuard, 실제 비전 모델
