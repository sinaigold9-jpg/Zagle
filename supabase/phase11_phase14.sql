-- Phase 11: notifications and notification settings.
-- Phase 14: device contacts and invitation support.
-- Run after schema.sql and auth_profile.sql. No secrets or mock rows.

create table if not exists notifications (
  id uuid primary key default uuid_generate_v4(), user_id uuid not null references auth.users(id) on delete cascade,
  type text not null, title text not null, body text not null, conversation_id uuid references conversations(id) on delete set null,
  read boolean not null default false, created_at timestamptz not null default now()
);
create index if not exists notifications_user_created_idx on notifications(user_id, created_at desc);
alter table notifications enable row level security;
drop policy if exists "notifications_owner_select" on notifications;
create policy "notifications_owner_select" on notifications for select using (auth.uid() = user_id);
drop policy if exists "notifications_owner_update" on notifications;
create policy "notifications_owner_update" on notifications for update using (auth.uid() = user_id) with check (auth.uid() = user_id);

create table if not exists notification_preferences (
  user_id uuid primary key references auth.users(id) on delete cascade,
  enabled boolean not null default true, messages boolean not null default true,
  groups boolean not null default true, rooms boolean not null default true,
  announcements boolean not null default true, updated_at timestamptz not null default now()
);
alter table notification_preferences enable row level security;
drop policy if exists "notification_preferences_owner" on notification_preferences;
create policy "notification_preferences_owner" on notification_preferences for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- Contacts remain on-device. The app requests READ_CONTACTS only when the user opens Contacts.
-- Invitations use Android's native share sheet; no address-book data is uploaded.
