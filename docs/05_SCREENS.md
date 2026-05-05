# 05. 화면별 명세

각 화면의 라우트, 진입 조건, 주요 상태, 인터랙션을 정리합니다.
**프로토타입 `갑빠왕_prototype_v2.html`이 시각적 단일 진실 공급원**입니다.

## 라우트 트리

```
GabpaNavGraph
├── splash
├── onboarding/
│   ├── intro
│   ├── login          (카카오/구글/이메일)
│   ├── signup         (이메일 시)
│   └── goal           (닉네임 + 목표 + 신체정보)
├── main (BottomNav 호스트)
│   ├── home
│   ├── record         (탭: 1rm, calendar, stats)
│   ├── challenge
│   ├── character
│   └── settings
├── workout/
│   ├── start          (모드 선택)
│   ├── running        (카운팅 중)
│   └── result/{sessionId}
├── levelup/{toStage}
├── pr/{newRecord}
└── notifications
```

## 화면별 명세

### Splash
- **상태**: 인증 토큰 확인
- **이동**: 토큰 있음 + 온보딩 완료 → home / 토큰 있음 + 온보딩 미완 → goal / 토큰 없음 → intro
- **시간**: 최대 1.5초 (네트워크 실패 시 즉시 intro)

### Onboarding/Intro
- 갑빠왕 슬로건 + "시작하기" 버튼
- "시작하기" → login

### Login
- 카카오/구글/이메일 3개 버튼
- 카카오: KakaoSDK → ID 토큰 → Supabase Edge Function `kakao-auth` 호출 → JWT 받기
- 구글: Supabase OAuth signInWith(Provider.Google)
- 이메일: signup 또는 signInWith(email, password)

### Goal Setup (온보딩 완료 화면)
- 닉네임 (필수)
- 목표: 자유 운동 / 체중 감량 / 근육 증진 (라디오)
- 키, 몸무게 (선택)
- 저장 → profiles에 INSERT → home

### Home (`/main/home`)
- ViewModel이 `profiles` 1행 + 최근 1주 daily_summary 가져옴
- 캐릭터 (현재 charStage) + 진화 진행도 바
- 스탯 카드 3개 (1회 최고 / 누적 / 연속) — **각 카드 탭 시 record 화면의 해당 탭으로 이동**
  - 1회 최고 → record?tab=1rm
  - 누적 → record?tab=stats
  - 연속 → record?tab=calendar
- 알림 벨 → notifications
- 캐릭터 영역 탭 → character
- "운동 시작" → workout/start

### Workout Start
- 모드 카드 3개 (자유 / 세트별 목표 / 갑빠 챌린지)
- 세트별 목표: 세트당 횟수 입력 (default [30,25,20])
- 시작 → workout/running

### Workout Running ⭐ (핵심)
- 카메라 프리뷰 (전면 / 후면 토글)
- 비전 카운터 모듈에서 카운트 이벤트 수신 → 상태 업데이트
- 수동 카운트 백업 버튼 (모델 실패 시)
- 음성 피드백 ("하나, 둘…" / TTS)
- 세트 종료: 자동(쉬는 시간 진입) or 수동(다음 세트 버튼)
- 세트 간 휴식 타이머 (default 60초, 스킵 가능)
- 운동 종료: 모든 세트 완료 or "그만" 버튼
- 종료 → workout/result/{sessionId}
- **상세는 `06_PUSHUP_COUNTER.md` 참조**

### Workout Result
- 총 횟수, 세트별 분포, 운동 시간
- 진화 진행도 바 (현재 누적 + 이번 운동 = 다음 단계까지 N개 남음)
- PR 갱신 시 → pr 화면 push
- 단계 진화 발생 시 → levelup 화면 push
- "홈으로" → home

### Level Up
- 캐릭터 애니메이션 (Lottie)
- "{N}단계 달성!" + 단계명 + "누적 {threshold}개 돌파"
- "계속 성장하기" → home
- "카카오로 자랑하기" → 바텀시트
  - 미리보기 카드 (캐릭터 + N단계 + 누적 + 닉네임)
  - 카카오톡으로 보내기 → KakaoLinkClient.shareDefault()
  - 이미지로 저장 → Storage `share-cards/` + 갤러리 저장

### Record (탭 호스트)
- 상단 탭: 1rm / calendar / stats
- `initialTab` 파라미터로 진입 시 탭 결정 (home의 스탯 카드에서 점프)

#### 1rm 탭
- 큰 숫자: 현재 1회 최고 (best_single)
- 트렌드 차트 (월별 1회 최고)
- 히스토리 리스트 (최근 갱신 5건)

#### Calendar 탭
- 월 단위 히트맵 (셀 = 일자 좌상단 작게, 횟수 중앙 크게 + "회")
- 강도 레전드
- 셀 탭 → 해당 일자 세트별 상세 펼침
- 좌우 화살표로 월 이동 (미래 비활성)

#### Stats 탭
- 누적 카드, 1회 최고 카드, 운동일 카드, 연속 카드
- 월별 1회 최고 성장 막대그래프
- 시간대별 운동 분포 (선택)

### Character
- 큰 캐릭터 (현재 단계, 애니메이션)
- 닉네임 + "누적 {N}개"
- 진행 바 (단계 → 단계, "레벨 업까지 N개 남음")
- 진화 로드맵 (9단계 카드 가로 스크롤)
  - done: 통과한 단계 (불투명)
  - current: 현재 단계 (강조)
  - locked: 미달성 (반투명 + 자물쇠)

### Challenge
- 활성 챌린지 카드 (참여 중) — 진행도 바
- 추천 챌린지 그리드 (7일 / 30일 / 100개 / 1000개)
- 카드 탭 → 상세 시트 (참여하기 / 취소)

### Notifications
- 타입별 아이콘: 🏆 PR / ⚡ 레벨업 / 🔥 연속 위기 / 🔔 리마인더
- 탭 시 read_at 갱신 + 해당 화면으로 점프

### Settings
- 프로필 섹션
- 알림 토글 (리마인더, 연속 위기, PR 갱신)
- 운동 설정 (음성 카운트, 카운트 단위)
- 계정 (로그아웃, 회원 탈퇴)
- 정보 (버전, 개인정보처리방침)

## 공통 규칙

- **탭 화면 (home, record, challenge, character, settings)에는 뒤로가기 없음**
- **푸시 화면 (workout/*, levelup, pr, notifications)에는 좌상단 ←**
- **시스템 백버튼**: 푸시 = pop, 탭 루트 = 앱 종료 확인
- **로딩**: 화면 진입 시 데이터 없으면 스켈레톤 (브랜드 톤 유지)
- **오프라인 인디케이터**: 상단에 얇은 노란 띠 + "오프라인 — 자동 동기화될 거예요"
