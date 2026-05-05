# 갑빠왕 — 개발 핸드오프 패키지

이 폴더는 **갑빠왕** 안드로이드 앱을 Claude Code로 개발하기 위한 명세 모음입니다.

## 어떻게 사용하나요?

1. 이 `handoff/` 폴더 전체와 `갑빠왕_prototype_v2.html`을 새 Android 프로젝트 루트에 복사
2. Claude Code 실행: `claude` (해당 폴더에서)
3. `08_CLAUDE_CODE_PROMPTS.md`에 있는 단계별 프롬프트를 순서대로 사용

## 문서 구조

| 파일 | 내용 | 언제 읽나요 |
|---|---|---|
| `01_PROJECT_SPEC.md` | 제품 개요, 핵심 원칙, 기능 목록 | 시작할 때 한 번 |
| `02_TECH_STACK.md` | Kotlin/Compose/Supabase 등 기술 스택 | 프로젝트 셋업 시 |
| `03_DESIGN_TOKENS.md` | 색상, 폰트, 간격, 컴포넌트 토큰 | 테마 만들 때 |
| `04_DATA_MODEL.md` | Supabase 테이블 스키마 + RLS 정책 | DB 셋업 시 |
| `05_SCREENS.md` | 화면별 명세 (라우트, 상태, 인터랙션) | 화면 만들 때마다 |
| `06_PUSHUP_COUNTER.md` | 비전 모델 통합 명세 | 카운팅 모듈 통합 시 |
| `07_MILESTONES.md` | 4단계 마일스톤 + 우선순위 | 계획 시 |
| `08_CLAUDE_CODE_PROMPTS.md` | 즉시 사용 가능한 프롬프트 모음 | Claude Code 띄울 때마다 |

## 핵심 의사결정 요약

- **플랫폼**: Android only (Kotlin + Jetpack Compose, Material3)
- **백엔드**: Supabase (Postgres + Auth + Storage)
- **인증**: 카카오 / 구글 / 이메일
- **운동 카운팅**: 사용자 보유 비전 모델 (카메라 셀카 모드, 엎드린 자세 인식)
- **소셜**: MVP에서는 친구 기능 제외, 카카오 공유만
- **수익**: 프리미엄 구독 (AI 코칭, 무제한 캐릭터 진화 등)
- **출시 전략**: 최소 기능부터 단계적 확장

## 디자인 레퍼런스

`갑빠왕_prototype_v2.html`을 브라우저에서 열어 인터랙션과 비주얼을 확인하세요.
이 프로토타입이 **단일 진실 공급원 (Source of Truth)**입니다.
이 문서들과 프로토타입이 충돌하면 **프로토타입이 우선**입니다.
