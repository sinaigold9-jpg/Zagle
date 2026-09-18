-- Account profile fields used by the native registration flow.
-- Run after schema.sql. No secrets or seed data are included.

alter table profiles add column if not exists first_name text;
alter table profiles add column if not exists last_name text;
alter table profiles add column if not exists phone text;
alter table profiles add column if not exists auth_method text;

create or replace function public.handle_new_user_profile()
returns trigger
language plpgsql
security definer set search_path = public
as $$
begin
  insert into public.profiles (id, username, display_name, first_name, last_name, phone, auth_method)
  values (
    new.id,
    nullif(new.raw_user_meta_data ->> 'username', ''),
    nullif(new.raw_user_meta_data ->> 'full_name', ''),
    nullif(new.raw_user_meta_data ->> 'first_name', ''),
    nullif(new.raw_user_meta_data ->> 'last_name', ''),
    new.phone,
    case when new.email is not null then 'email' when new.phone is not null then 'phone' end
  )
  on conflict (id) do update set
    display_name = coalesce(excluded.display_name, profiles.display_name),
    first_name = coalesce(excluded.first_name, profiles.first_name),
    last_name = coalesce(excluded.last_name, profiles.last_name),
    phone = coalesce(excluded.phone, profiles.phone),
    auth_method = coalesce(excluded.auth_method, profiles.auth_method),
    updated_at = now();
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
after insert on auth.users
for each row execute procedure public.handle_new_user_profile();
