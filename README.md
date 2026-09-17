# Zajel — Phase 4 complete

Native Android source only. This revision reviews and hardens phases 1–2, then implements exactly the two requested phases:

- Phase 3: text-message sending plus persisted message status (`SENT`, `DELIVERED`, `READ`) and status update API.
- Phase 4: Android native document picker, local cache while uploading, real Supabase Storage upload through a replaceable repository boundary, attachment metadata, and cleanup of the temporary local copy after upload.

There are no WebViews, fake users, fake conversations, fake messages, or placeholder production data. Empty server results remain empty states.

## Review fixes included
- Sign-in session token is encrypted with Android Keystore AES-GCM.
- Supabase networking remains outside the UI layer and all network work is off the main thread.
- The client accepts empty configuration honestly and reports configuration errors.
- UI reads authenticated server data only.

## Required server schema and RLS
Existing tables: `profiles`, `conversations`, `conversation_members`, `messages`.

For this revision, `messages` should include `message_type` and `status` (`SENT`, `DELIVERED`, `READ`), and an `attachments` table should include `id`, `message_id`, `name`, `mime_type`, `storage_path`, and `size_bytes`. Configure RLS so a user may only read memberships/messages/attachments for conversations they belong to, insert their own messages, and update statuses according to your policy.

Create a private Storage bucket named `zajel-media` and matching Storage RLS policies. The app uploads only after authentication and never includes a service-role key. The storage adapter is isolated in `SupabaseClient`, so it can be replaced later by an independent provider without changing presentation/domain code.

## Termux/Ubuntu build
Use the locally installed SDK/Gradle setup; do not commit `local.properties`, credentials, keystores, or generated build output. Example:

```sh
./gradlew assembleDebug -PzajelSupabaseUrl=https://YOUR_PROJECT.supabase.co -PzajelSupabaseAnonKey=YOUR_PUBLIC_ANON_KEY
```

The source targets Java/JDK 17-compatible Android APIs and does not require Android Studio.

Development plan: 20 stages total; phases 1–4 are now complete, so 16 stages remain.
