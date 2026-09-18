-- Public discovery schema for phases 9 and 10.
-- Run after schema.sql. No seed or mock rows are inserted.

alter table profiles add column if not exists is_discoverable boolean not null default false;

create table if not exists explore_suggestions (
  id uuid primary key default uuid_generate_v4(), type text not null,
  title text not null, reason text, rank integer not null default 0,
  enabled boolean not null default true, created_at timestamptz not null default now()
);
create table if not exists public_ads (
  id uuid primary key default uuid_generate_v4(), title text not null,
  body text not null, target_url text, priority integer not null default 0,
  enabled boolean not null default false, created_at timestamptz not null default now()
);

alter table explore_suggestions enable row level security;
alter table public_ads enable row level security;

 drop policy if exists "profiles_select_discoverable" on profiles;
create policy "profiles_select_discoverable" on profiles for select
  using (auth.uid() is not null and is_discoverable = true);
drop policy if exists "public_suggestions_read_authenticated" on explore_suggestions;
create policy "public_suggestions_read_authenticated" on explore_suggestions for select
  using (auth.uid() is not null and enabled = true);
drop policy if exists "public_ads_read_authenticated" on public_ads;
create policy "public_ads_read_authenticated" on public_ads for select
  using (auth.uid() is not null and enabled = true);

create index if not exists groups_public_title_idx on groups using gin (to_tsvector('simple', coalesce(title,'') || ' ' || coalesce(description,''))) where is_private = false;
create index if not exists rooms_public_title_idx on rooms using gin (to_tsvector('simple', coalesce(title,'') || ' ' || coalesce(description,''))) where is_private = false;
create index if not exists profiles_discoverable_username_idx on profiles (username) where is_discoverable = true;
