# 07. 마일스톤 (출시 가능한 최소 → 단계적 확장)

## 전략

> "한 번에 다 만들지 말고, **써볼 수 있는 앱을 매주 만들기**"

## 스프린트 구성 (1주 단위, 4주 = 베타 출시 목표)

### 🥚 Sprint 1 — Bootstrap & Auth (1주)
**목표**: 앱 켜서 로그인하면 빈 홈이 보임
- [ ] Android 프로젝트 세팅 (Compose, Hilt, Navigation, Room)
- [ ] Supabase 프로젝트 + 테이블 생성 (04_DATA_MODEL.md 실행)
- [ ] 디자인 토큰 (Theme.kt, Color.kt, Type.kt)
- [ ] 공통 컴포넌트 5개 (PrimaryButton, GhostButton, ProgressBar, CharacterImage, BottomNav)
- [ ] Splash → Login → Goal Setup → Home (빈 껍데기)
- [ ] 카카오/구글/이메일 인증 모두 동작
- ✅ **Done 정의**: 신규 가입 → 닉네임 입력 → 로그인 상태로 빈 홈 진입

### 🐣 Sprint 2 — Workout Core (1주)
**목표**: 운동 1회를 끝까지 수행하고 기록이 남음
- [ ] Workout Start (모드 선택)
- [ ] Workout Running (가짜 카운터로 동작)
- [ ] Workout Result
- [ ] Supabase에 session/sets 저장 + 트리거 작동 확인
- [ ] 홈에 누적/1회최고/연속 실시간 반영
- [ ] Room 캐싱 + 오프라인 동기화 (WorkManager)
- ✅ **Done 정의**: 가짜 카운트로 3세트 운동 → 결과 저장 → 홈 새로고침 시 누적 증가

### 🐤 Sprint 3 — Vision Counter & Record (1주)
**목표**: 진짜 카메라로 카운팅 + 기록 화면 완성
- [ ] PushupCounter 인터페이스 + 사용자 보유 모델 통합
- [ ] CameraX 프리뷰 + ImageAnalysis 파이프라인
- [ ] 권한 흐름, 폴백 (수동 +1)
- [ ] Record 화면 3탭 (1rm / calendar / stats)
- [ ] Calendar 히트맵 (개선된 셀 디자인)
- [ ] 통계 차트 (Vico)
- ✅ **Done 정의**: 실제 푸쉬업 → 카운트 → 캘린더에 그날 횟수 표시

### 🦅 Sprint 4 — Character & Polish (1주)
**목표**: 캐릭터 진화 + 카카오 공유 + 베타 배포
- [ ] Character 화면 + 진화 로드맵
- [ ] LevelUp 화면 (Lottie 애니메이션)
- [ ] 카카오톡 공유 카드 (KakaoLinkClient)
- [ ] PR 갱신 화면
- [ ] Notifications (인앱 + FCM 푸시 베이스)
- [ ] Settings
- [ ] Crashlytics, Analytics
- [ ] 내부 테스트 트랙 배포
- ✅ **Done 정의**: 가족/친구 5명에게 베타 링크 보내기

## 베타 후 → V1 출시 (2~4주)
- [ ] 챌린지 시스템 (Tier 2)
- [ ] 프리미엄 구독 (Google Play Billing)
- [ ] AI 코칭 (자세 점수 기반 피드백)
- [ ] 캐릭터 커스터마이징 (스킨)
- [ ] 위젯
- [ ] 정식 출시

## 진행 추적

각 스프린트 끝에 다음을 기록:
- ✅ 완료 항목
- ⚠️ 미뤄진 항목과 사유
- 🐛 발견된 버그 (별도 이슈로)
- 📊 베타 사용자 피드백 (Sprint 4 이후)

## 일정 가이드

- **풀타임 1인 개발**: 4주 → 베타 / 8주 → V1
- **사이드 프로젝트 (주 10~15h)**: 8~10주 → 베타 / 16주 → V1
- **AI 페어 (Claude Code 적극 활용)**: 절반 단축 가능. 단, **검수 시간**은 줄지 않음

## 위험 요소 & 대응

| 위험 | 영향 | 대응 |
|---|---|---|
| 비전 모델 정확도 부족 | 카운트 오류 → 사용자 이탈 | Sprint 3에서 5명 테스트, 수동 +1 백업 항상 노출 |
| 카카오 OAuth Edge Function 실패 | 로그인 막힘 | 구글/이메일을 먼저, 카카오는 Sprint 1 후반에 |
| Play Store 심사 (카메라 사유) | 출시 지연 | Privacy 명세 + 영상 서버 비전송 명시 |
| Supabase 무료 한도 (DB 500MB) | 사용자 1만+ 시 부족 | 모니터링 후 Pro($25/mo) 전환 |
