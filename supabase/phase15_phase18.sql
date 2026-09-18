-- Phase 15 + Phase 18 support tables. No secrets, no mock rows, no WebView.
create table if not exists privacy_settings (
  user_id uuid primary key references auth.users(id) on delete cascade,
  profile_visible boolean not null default true,
  last_seen_visible boolean not null default true,
  read_receipts boolean not null default true,
  contacts_invites boolean not null default true,
  allow_group_invites boolean not null default true,
  updated_at timestamptz not null default now()
);

create table if not exists device_sessions (
  id uuid primary key default uuid_generate_v4(),
  user_id uuid not null references auth.users(id) on delete cascade,
  device_name text not null default 'Android',
  platform text not null default 'Android',
  is_current boolean not null default false,
  revoked boolean not null default false,
  last_seen_at timestamptz not null default now(),
  created_at timestamptz not null default now()
);

create table if not exists security_events (
  id uuid primary key default uuid_generate_v4(),
  user_id uuid not null references auth.users(id) on delete cascade,
  event_type text not null,
  details jsonb default '{}'::jsonb,
  created_at timestamptz not null default now()
);

alter table privacy_settings enable row level security;
alter table device_sessions enable row level security;
alter table security_events enable row level security;

create policy if not exists privacy_settings_owner on privacy_settings
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy if not exists device_sessions_owner_select on device_sessions
  for select using (auth.uid() = user_id);

create policy if not exists device_sessions_owner_update on device_sessions
  for update using (auth.uid() = user_id) with check (auth.uid() = user_id);

create policy if not exists security_events_owner on security_events
  for insert with check (auth.uid() = user_id);

create index if not exists device_sessions_user_last_seen_idx on device_sessions(user_id, last_seen_at desc);
create index if not exists security_events_user_created_idx on security_events(user_id, created_at desc);

-- Architecture note: keep all business logic in repositories and expose view-only activities.
-- No mock data or web-based screens are introduced here.
