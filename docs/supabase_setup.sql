-- Run this in Supabase Dashboard > SQL Editor

-- Workout sessions table
create table if not exists workout_sessions (
  id uuid default gen_random_uuid() primary key,
  user_id uuid references auth.users not null default auth.uid(),
  date_millis bigint not null,
  total_reps int not null,
  sets int not null,
  duration_sec int not null,
  created_at timestamptz default now()
);

-- Row Level Security: users can only access their own data
alter table workout_sessions enable row level security;

create policy "Users can manage own sessions"
  on workout_sessions
  for all
  using (auth.uid() = user_id)
  with check (auth.uid() = user_id);

-- Allow anonymous users to insert
create policy "Allow anonymous inserts"
  on workout_sessions
  for insert
  to anon
  with check (true);

-- Leaderboard view (public total reps per user)
create or replace view public.leaderboard as
  select user_id, sum(total_reps) as total_reps, count(*) as session_count
  from workout_sessions
  group by user_id
  order by total_reps desc;
