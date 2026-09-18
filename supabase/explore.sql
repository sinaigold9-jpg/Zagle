-- Public discovery additions for phases 9 and 10. No seed/mock rows are inserted.

create table if not exists explore_suggestions (
  id uuid primary key default uuid_generate_v4(),
  type text not null,
  title text not null,
  reason text,
  rank integer default 0,
  enabled boolean default true,
  created_at timestamptz default now()
);

create table if not exists public_ads (
  id uuid primary key default uuid_generate_v4(),
  title text not null,
  body text not null,
  target_url text,
  priority integer default 0,
  enabled boolean default false,
  created_at timestamptz default now()
);

alter table explore_suggestions enable row level security;
alter table public_ads enable row level security;

create policy if not exists "public_suggestions_read_authenticated" on explore_suggestions
  for select using (auth.uid() is not null and enabled = true);

create policy if not exists "public_ads_read_authenticated" on public_ads
  for select using (auth.uid() is not null and enabled = true);

-- Do not insert seed rows here. Populate these tables only with reviewed public content.
