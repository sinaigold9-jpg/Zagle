# Zajel — Supabase schema and native integration

This project keeps all Android UI and business logic native to Android. Any backend connection is isolated behind Supabase client boundaries and never embedded in views or screens.

## Database and Storage blueprint

The Supabase project should include the following tables:

- profiles
- conversations
- conversation_members
- messages
- attachments
- groups
- group_members
- rooms
- room_members
- room_messages
- storage bucket: `zajel-media`

The `supabase/schema.sql` file contains the core CREATE TABLE statements and RLS policies for the app data model. It is intentionally generic and safe for a local project setup. No secrets are committed here.

## Required local build variables

Run Gradle locally with the public values only:

```sh
./gradlew assembleDebug \
  -PzajelSupabaseUrl=https://YOUR_PROJECT.supabase.co \
  -PzajelSupabaseAnonKey=YOUR_PUBLIC_ANON_KEY
```

Do not commit `local.properties`, keystores, service-role keys, or generated APK/AAB files.

## Phase coverage

Phases completed in source as of this revision:
- Phase 1: Accounts, users, profile, and settings
- Phase 2: Home and direct/private chats
- Phase 3: Text send and message status
- Phase 4: Attachments, uploads, and local cache cleanup
- Phase 5: Groups and member administration
- Phase 8: Rooms and room messaging

The project remains in a native Java Android architecture and the design remains compatible with Termux + Ubuntu ARM64 and JDK 17 tooling.
