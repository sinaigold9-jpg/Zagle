drop policy if exists "profiles_update_own" on profiles;
create policy "profiles_update_own" on profiles
  for update using (auth.uid() = id) with check (auth.uid() = id);

drop policy if exists "profiles_insert_own" on profiles;
create policy "profiles_insert_own" on profiles
  for insert with check (auth.uid() = id);

drop policy if exists "member_conversations_select" on conversation_members;
create policy "member_conversations_select" on conversation_members
  for select using (auth.uid() = user_id);

drop policy if exists "member_conversations_insert" on conversation_members;
create policy "member_conversations_insert" on conversation_members
  for insert with check (auth.uid() = user_id);

drop policy if exists "messages_select_member" on messages;
create policy "messages_select_member" on messages
  for select using (
    exists (
      select 1 from conversation_members cm
      where cm.conversation_id = messages.conversation_id and cm.user_id = auth.uid()
    )
  );

drop policy if exists "messages_insert_own" on messages;
create policy "messages_insert_own" on messages
  for insert with check (auth.uid() = sender_id);

drop policy if exists "messages_update_own_status" on messages;
create policy "messages_update_own_status" on messages
  for update using (auth.uid() = sender_id) with check (auth.uid() = sender_id);

drop policy if exists "attachments_select_member" on attachments;
create policy "attachments_select_member" on attachments
  for select using (
    exists (
      select 1 from messages m
      join conversation_members cm on cm.conversation_id = m.conversation_id
      where m.id = attachments.message_id and cm.user_id = auth.uid()
    )
  );

drop policy if exists "attachments_insert_own" on attachments;
create policy "attachments_insert_own" on attachments
  for insert with check (
    exists (
      select 1 from messages m
      where m.id = attachments.message_id and m.sender_id = auth.uid()
    )
  );

drop policy if exists "groups_select_member" on groups;
create policy "groups_select_member" on groups
  for select using (
    exists (
      select 1 from group_members gm where gm.group_id = groups.id and gm.user_id = auth.uid()
    ) or groups.owner_id = auth.uid() or groups.is_private = false
  );

drop policy if exists "groups_insert_own" on groups;
create policy "groups_insert_own" on groups
  for insert with check (auth.uid() = owner_id);

drop policy if exists "group_members_select_member" on group_members;
create policy "group_members_select_member" on group_members
  for select using (
    exists (
      select 1 from group_members gm where gm.group_id = group_members.group_id and gm.user_id = auth.uid()
    ) or auth.uid() = group_members.user_id
  );

drop policy if exists "group_members_insert_own_or_admin" on group_members;
create policy "group_members_insert_own_or_admin" on group_members
  for insert with check (
    auth.uid() = user_id or exists (
      select 1 from group_members gm
      where gm.group_id = group_members.group_id and gm.user_id = auth.uid() and gm.role in ('owner', 'admin')
    )
  );

drop policy if exists "rooms_select_member" on rooms;
create policy "rooms_select_member" on rooms
  for select using (
    exists (
      select 1 from room_members rm where rm.room_id = rooms.id and rm.user_id = auth.uid()
    ) or rooms.owner_id = auth.uid() or rooms.is_private = false
  );

drop policy if exists "rooms_insert_own" on rooms;
create policy "rooms_insert_own" on rooms
  for insert with check (auth.uid() = owner_id);

drop policy if exists "room_members_select_member" on room_members;
create policy "room_members_select_member" on room_members
  for select using (
    exists (
      select 1 from room_members rm where rm.room_id = room_members.room_id and rm.user_id = auth.uid()
    ) or auth.uid() = room_members.user_id
  );

drop policy if exists "room_members_insert_own_or_admin" on room_members;
create policy "room_members_insert_own_or_admin" on room_members
  for insert with check (
    auth.uid() = user_id or exists (
      select 1 from room_members rm
      where rm.room_id = room_members.room_id and rm.user_id = auth.uid() and rm.role in ('owner', 'admin')
    )
  );
