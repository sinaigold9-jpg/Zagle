-- Phases 12 and 13 schema additions. Run after existing migrations.
-- Backup files are selected by the user through Android Storage Access Framework.
-- No service-role key, passwords, or file bytes are stored here.

create table if not exists backup_preferences (
  user_id uuid primary key references auth.users(id) on delete cascade,
  enabled boolean not null default false,
  frequency text not null default 'manual' check (frequency in ('manual','daily','weekly','monthly','every_90_days')),
  include_messages boolean not null default true,
  include_media boolean not null default false,
  include_files boolean not null default true,
  updated_at timestamptz not null default now()
);
alter table backup_preferences enable row level security;
drop policy if exists "backup_preferences_owner" on backup_preferences;
create policy "backup_preferences_owner" on backup_preferences for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

create table if not exists file_records (
  id uuid primary key default uuid_generate_v4(), user_id uuid not null references auth.users(id) on delete cascade,
  original_name text not null, mime_type text, size_bytes bigint not null default 0,
  storage_provider text not null default 'device', storage_path text not null,
  created_at timestamptz not null default now()
);
create index if not exists file_records_user_created_idx on file_records(user_id, created_at desc);
alter table file_records enable row level security;
drop policy if exists "file_records_owner" on file_records;
create policy "file_records_owner" on file_records for all using (auth.uid() = user_id) with check (auth.uid() = user_id);
