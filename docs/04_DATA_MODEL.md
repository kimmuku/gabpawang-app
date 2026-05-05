# 04. 데이터 모델 (Supabase)

## 테이블 스키마

Supabase SQL Editor에서 그대로 실행할 수 있습니다.

```sql
-- ============================================
-- USERS / PROFILES
-- ============================================
-- auth.users는 Supabase가 자동 생성. 우리는 profiles에 부가 정보 저장.

create table public.profiles (
  id              uuid primary key references auth.users(id) on delete cascade,
  nickname        text not null,
  avatar_url      text,
  height_cm       int,
  weight_kg       numeric(5,2),
  goal            text check (goal in ('free','weight_loss','muscle_gain')),
  char_stage      int not null default 1 check (char_stage between 1 and 9),
  total_pushups   int not null default 0,
  best_single     int not null default 0,
  current_streak  int not null default 0,
  longest_streak  int not null default 0,
  premium_until   timestamptz,
  created_at      timestamptz not null default now(),
  updated_at      timestamptz not null default now()
);

create index profiles_total_pushups_idx on public.profiles(total_pushups desc);

-- ============================================
-- WORKOUT SESSIONS (운동 1회 = 여러 세트)
-- ============================================
create table public.workout_sessions (
  id              uuid primary key default gen_random_uuid(),
  user_id         uuid not null references auth.users(id) on delete cascade,
  mode            text not null check (mode in ('free','target','challenge')),
  target_count    int[],          -- 모드=target일 때 [30,25,20]
  target_sets     int,
  total_count     int not null default 0,
  duration_sec    int,
  started_at      timestamptz not null,
  ended_at        timestamptz,
  is_pr           boolean not null default false,
  best_set        int,            -- 1회 최고 세트
  notes           text,
  created_at      timestamptz not null default now()
);

create index sessions_user_started_idx on public.workout_sessions(user_id, started_at desc);

-- ============================================
-- WORKOUT SETS (세트 1개)
-- ============================================
create table public.workout_sets (
  id              uuid primary key default gen_random_uuid(),
  session_id      uuid not null references public.workout_sessions(id) on delete cascade,
  user_id         uuid not null references auth.users(id) on delete cascade,
  set_number      int not null,
  target_count    int,
  actual_count    int not null,
  rest_sec        int,
  cadence_avg_ms  int,            -- 평균 1회당 ms (비전 카운트가 측정)
  form_score      numeric(3,2),   -- 0.00~1.00 (자세 점수, 향후 AI 코칭)
  created_at      timestamptz not null default now()
);

create index sets_session_idx on public.workout_sets(session_id, set_number);

-- ============================================
-- DAILY AGGREGATES (캘린더 히트맵용)
-- ============================================
create table public.daily_summary (
  user_id         uuid not null references auth.users(id) on delete cascade,
  date            date not null,
  total_count     int not null default 0,
  session_count   int not null default 0,
  best_set        int not null default 0,
  primary key (user_id, date)
);

create index daily_user_date_idx on public.daily_summary(user_id, date desc);

-- ============================================
-- CHALLENGES
-- ============================================
create table public.challenges (
  id              uuid primary key default gen_random_uuid(),
  slug            text not null unique,  -- '7day','30day','100','1000' 등
  title           text not null,
  description     text,
  goal_type       text not null check (goal_type in ('streak_days','total_count','single_count','daily_count')),
  goal_value      int not null,
  duration_days   int,                   -- streak_days/daily_count 시 사용
  reward_text     text,
  is_active       boolean not null default true
);

create table public.challenge_progress (
  user_id         uuid not null references auth.users(id) on delete cascade,
  challenge_id    uuid not null references public.challenges(id) on delete cascade,
  joined_at       timestamptz not null default now(),
  current_value   int not null default 0,
  completed_at    timestamptz,
  primary key (user_id, challenge_id)
);

-- ============================================
-- NOTIFICATIONS (인앱 알림)
-- ============================================
create table public.notifications (
  id              uuid primary key default gen_random_uuid(),
  user_id         uuid not null references auth.users(id) on delete cascade,
  type            text not null check (type in ('pr','levelup','streak_warn','reminder','challenge_done','system')),
  title           text not null,
  body            text,
  payload         jsonb,
  read_at         timestamptz,
  created_at      timestamptz not null default now()
);

create index notif_user_created_idx on public.notifications(user_id, created_at desc);

-- ============================================
-- LEVEL UP HISTORY
-- ============================================
create table public.level_up_history (
  id              uuid primary key default gen_random_uuid(),
  user_id         uuid not null references auth.users(id) on delete cascade,
  from_stage      int not null,
  to_stage        int not null,
  total_at_levelup int not null,
  occurred_at     timestamptz not null default now()
);
```

## RLS (Row Level Security)

모든 테이블에 RLS 활성화. 본인 데이터만 접근 가능.

```sql
-- profiles
alter table public.profiles enable row level security;

create policy "본인 프로필 조회"  on public.profiles for select using (auth.uid() = id);
create policy "본인 프로필 수정"  on public.profiles for update using (auth.uid() = id);
create policy "본인 프로필 생성"  on public.profiles for insert with check (auth.uid() = id);

-- workout_sessions
alter table public.workout_sessions enable row level security;
create policy "본인 운동 조회" on public.workout_sessions for select using (auth.uid() = user_id);
create policy "본인 운동 추가" on public.workout_sessions for insert with check (auth.uid() = user_id);
create policy "본인 운동 수정" on public.workout_sessions for update using (auth.uid() = user_id);
create policy "본인 운동 삭제" on public.workout_sessions for delete using (auth.uid() = user_id);

-- workout_sets
alter table public.workout_sets enable row level security;
create policy "본인 세트 조회" on public.workout_sets for select using (auth.uid() = user_id);
create policy "본인 세트 추가" on public.workout_sets for insert with check (auth.uid() = user_id);

-- daily_summary
alter table public.daily_summary enable row level security;
create policy "본인 요약 조회" on public.daily_summary for select using (auth.uid() = user_id);
create policy "본인 요약 갱신" on public.daily_summary for all using (auth.uid() = user_id);

-- challenges (전체 공개 읽기)
alter table public.challenges enable row level security;
create policy "챌린지 공개 조회" on public.challenges for select using (true);

-- challenge_progress
alter table public.challenge_progress enable row level security;
create policy "본인 챌린지 진행" on public.challenge_progress for all using (auth.uid() = user_id);

-- notifications
alter table public.notifications enable row level security;
create policy "본인 알림 조회" on public.notifications for select using (auth.uid() = user_id);
create policy "본인 알림 갱신" on public.notifications for update using (auth.uid() = user_id);

-- level_up_history
alter table public.level_up_history enable row level security;
create policy "본인 레벨업 조회" on public.level_up_history for select using (auth.uid() = user_id);
create policy "본인 레벨업 추가" on public.level_up_history for insert with check (auth.uid() = user_id);
```

## 트리거 / 함수

운동 세션 종료 시 daily_summary 자동 갱신 + 캐릭터 단계 자동 진화.

```sql
-- 세션 종료 시 누적/캐릭터 단계 갱신
create or replace function public.on_workout_session_complete()
returns trigger language plpgsql security definer as $$
declare
  new_total int;
  cur_stage int;
  new_stage int;
  thresholds int[] := array[0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000];
  i int;
begin
  if new.ended_at is null then return new; end if;
  if old.ended_at is not null then return new; end if;

  -- 누적 갱신
  update public.profiles
    set total_pushups = total_pushups + new.total_count,
        best_single = greatest(best_single, coalesce(new.best_set, 0)),
        updated_at = now()
    where id = new.user_id
    returning total_pushups, char_stage into new_total, cur_stage;

  -- 단계 계산
  new_stage := 1;
  for i in 1..array_length(thresholds, 1) loop
    if new_total >= thresholds[i] then new_stage := i; end if;
  end loop;

  if new_stage > cur_stage then
    update public.profiles set char_stage = new_stage where id = new.user_id;
    insert into public.level_up_history(user_id, from_stage, to_stage, total_at_levelup)
      values (new.user_id, cur_stage, new_stage, new_total);
    insert into public.notifications(user_id, type, title, body, payload)
      values (new.user_id, 'levelup', new_stage || '단계 달성!',
              '갑빠가 진화했어요. 카카오로 자랑해보세요.',
              jsonb_build_object('to_stage', new_stage));
  end if;

  -- daily_summary 업서트
  insert into public.daily_summary(user_id, date, total_count, session_count, best_set)
    values (new.user_id, (new.started_at at time zone 'Asia/Seoul')::date,
            new.total_count, 1, coalesce(new.best_set, 0))
    on conflict (user_id, date) do update set
      total_count = daily_summary.total_count + excluded.total_count,
      session_count = daily_summary.session_count + 1,
      best_set = greatest(daily_summary.best_set, excluded.best_set);

  return new;
end $$;

create trigger trg_workout_complete
  after update on public.workout_sessions
  for each row execute function public.on_workout_session_complete();
```

## Kotlin 도메인 모델 (참고)

```kotlin
data class Profile(
  val id: String,
  val nickname: String,
  val avatarUrl: String?,
  val charStage: Int,        // 1..9
  val totalPushups: Int,
  val bestSingle: Int,
  val currentStreak: Int,
  val premiumUntil: Instant?
)

data class WorkoutSession(
  val id: String,
  val mode: WorkoutMode,
  val totalCount: Int,
  val durationSec: Int,
  val startedAt: Instant,
  val isPr: Boolean,
  val sets: List<WorkoutSet>
)

enum class WorkoutMode { FREE, TARGET, CHALLENGE }

object StageThresholds {
  val values = intArrayOf(0, 100, 500, 2000, 5000, 10000, 25000, 50000, 100000)
  fun stageFor(total: Int): Int = values.indexOfLast { total >= it } + 1
  fun nextThreshold(stage: Int): Int? = values.getOrNull(stage)
}
```
