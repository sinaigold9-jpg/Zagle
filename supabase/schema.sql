-- Zajel Supabase schema for native app feature phases 1, 2, 3, 4, 5, and 8.
-- This file is intentionally SQL-only and does not contain secrets.

create extension if not exists "uuid-ossp";

create table if not exists profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique,
  display_name text,
  avatar_url text,
  created_at timestamptz default now(),
  updated_at timestamptz default now()
);

create table if not exists conversations (
  id uuid primary key default uuid_generate_v4(),
  title text,
  is_direct boolean default true,
  created_at timestamptz default now()
);

create table if not exists conversation_members (
  id uuid primary key default uuid_generate_v4(),
  conversation_id uuid references conversations(id) on delete cascade,
  user_id uuid references auth.users(id) on delete cascade,
  created_at timestamptz default now(),
  unique (conversation_id, user_id)
);

create table if not exists messages (
  id uuid primary key default uuid_generate_v4(),
  conversation_id uuid references conversations(id) on delete cascade,
  sender_id uuid references auth.users(id) on delete cascade,
  body text,
  message_type text default 'text',
  status text default 'SENT',
  created_at timestamptz default now()
);

create table if not exists attachments (
  id uuid primary key default uuid_generate_v4(),
  message_id uuid references messages(id) on delete cascade,
  name text,
  mime_type text,
  storage_path text,
  size_bytes bigint default 0,
  created_at timestamptz default now()
);

create table if not exists groups (
  id uuid primary key default uuid_generate_v4(),
  title text not null,
  description text,
  avatar_url text,
  is_private boolean default false,
  owner_id uuid references auth.users(id) on delete cascade,
  created_at timestamptz default now()
);

create table if not exists group_members (
  id uuid primary key default uuid_generate_v4(),
  group_id uuid references groups(id) on delete cascade,
  user_id uuid references auth.users(id) on delete cascade,
  role text default 'member',
  created_at timestamptz default now(),
  unique (group_id, user_id)
);

create table if not exists rooms (
  id uuid primary key default uuid_generate_v4(),
  title text not null,
  description text,
  is_private boolean default false,
  owner_id uuid references auth.users(id) on delete cascade,
  created_at timestamptz default now()
);

create table if not exists room_members (
  id uuid primary key default uuid_generate_v4(),
  room_id uuid references rooms(id) on delete cascade,
  user_id uuid references auth.users(id) on delete cascade,
  role text default 'member',
  created_at timestamptz default now(),
  unique (room_id, user_id)
);

create table if not exists room_messages (
  id uuid primary key default uuid_generate_v4(),
  room_id uuid references rooms(id) on delete cascade,
  sender_id uuid references auth.users(id) on delete cascade,
  body text,
  created_at timestamptz default now()
);

alter table profiles enable row level security;
alter table conversations enable row level security;
alter table conversation_members enable row level security;
alter table messages enable row level security;
alter table attachments enable row level security;
alter table groups enable row level security;
alter table group_members enable row level security;
alter table rooms enable row level security;
alter table room_members enable row level security;
alter table room_messages enable row level security;

create policy if not exists "profiles_select_own" on profiles
  for select using (auth.uid() = id);

create policy if not exists "profiles_update_own" on profiles
  for update using (auth.uid() = id) with check (auth.uid() = id);

create policy if not exists "profiles_insert_own" on profiles
  for insert with check (auth.uid() = id);

create policy if not exists "member_conversations_select" on conversation_members
  for select using (auth.uid() = user_id);

create policy if not exists "member_conversations_insert" on conversation_members
  for insert with check (auth.uid() = user_id);

create policy if not exists "messages_select_member" on messages
  for select using (
    exists (
      select 1 from conversation_members cm
      where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
    )
  );

create policy if not exists "messages_insert_own" on messages
  for insert with check (auth.uid() = sender_id);

create policy if not exists "messages_update_own_status" on messages
  for update using (auth.uid() = sender_id) with check (auth.uid() = sender_id);

create policy if not exists "attachments_select_member" on attachments
  for select using (
    exists (
      select 1 from messages m
      join conversation_members cm on cm.conversation_id = m.conversation_id
      where m.id = attachments.message_id and cm.user_id = auth.uid()
    )
  );

create policy if not exists "attachments_insert_own" on attachments
  for insert with check (
    exists (
      select 1 from messages m
      where m.id = attachments.message_id and m.sender_id = auth.uid()
    )
  );

create policy if not exists "groups_select_member" on groups
  for select using (
    exists (
      select 1 from group_members gm where gm.group_id = groups.id and gm.user_id = auth.uid()
    ) or groups.owner_id = auth.uid() or groups.is_private = false
  );

create policy if not exists "groups_insert_own" on groups
  for insert with check (auth.uid() = owner_id);

create policy if not exists "group_members_select_member" on group_members
  for select using (
    exists (
      select 1 from group_members gm where gm.group_id = group_members.group_id and gm.user_id = auth.uid()
    ) or auth.uid() = group_members.user_id
  );

create policy if not exists "group_members_insert_own_or_admin" on group_members
  for insert with check (
    auth.uid() = user_id or exists (
      select 1 from group_members gm
      where gm.group_id = group_members.group_id and gm.user_id = auth.uid() and gm.role in ('owner', 'admin')
    )
  );

create policy if not exists "rooms_select_member" on rooms
  for select using (
    exists (
      select 1 from room_members rm where rm.room_id = rooms.id and rm.user_id = auth.uid()
    ) or rooms.owner_id = auth.uid() or rooms.is_private = false
  );

create policy if not exists "rooms_insert_own" on rooms
  for insert with check (auth.uid() = owner_id);

create policy if not exists "room_members_select_member" on room_members
  for select using (
    exists (
      select 1 from room_members rm where rm.room_id = room_members.room_id and rm.user_id = auth.uid()
    ) or auth.uid() = room_members.user_id
  );

create policy if not exists "room_members_insert_own_or_admin" on room_members
  for insert with check (
    auth.uid() = user_id or exists (
      select 1 from room_members rm
      where rm.room_id = room_members.room_id and rm.user_id = auth.uid() and rm.role in ('owner', 'admin')
    )
  );

create policy if not exists "room_messages_select_member" on room_messages
  for select using (
    exists (
      select 1 from room_members rm where rm.room_id = room_messages.room_id and rm.user_id = auth.uid()
    )
  );

create policy if not exists "room_messages_insert_own" on room_messages
  for insert with check (auth.uid() = sender_id);

create policy if not exists "group_conversations_select" on conversations
  for select using (
    auth.uid() is not null
  );

create policy if not exists "group_conversations_insert" on conversations
  for insert with check (true);

-- Storage bucket creation should be done in Supabase dashboard or via CLI:
-- create storage bucket `zajel-media` with public=false;
-- and then add policies allowing authenticated users to upload files and read their own objects.

