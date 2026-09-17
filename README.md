# Phase 1: Accounts, users, profile, and basic settings

This is the first native Android source implementation of Zajel 1.1.0.0.

- 100% native Java Android UI; no WebView, PWA, HTML, or JavaScript.
- Phase scope is intentionally limited to accounts/users and profile/basic settings.
- Supabase is accessed through a replaceable data-source boundary using Android's native `HttpURLConnection`.
- No credentials, signing files, tokens, or fake production data are committed.

## Local configuration
Pass the project URL and public anon key as Gradle properties (or adapt the local build command):

```sh
./gradlew assembleDebug -PzajelSupabaseUrl=https://YOUR_PROJECT.supabase.co -PzajelSupabaseAnonKey=YOUR_PUBLIC_ANON_KEY
```

If configuration is absent, the app remains usable as an honest signed-out shell and displays a configuration error rather than inventing users or content.

## Expected profile table
The profile endpoint expects a `profiles` table with at least `id` (UUID, matching `auth.users.id`), `username`, `display_name`, and `avatar_url`. Configure RLS policies in Supabase before testing profile reads/writes. The app does not contain service-role credentials.

## Architecture
`presentation` owns screens/state, `domain` owns models/contracts, `data` owns Supabase and secure session storage, and `core` owns configuration/network primitives. The repository can later be replaced without rewriting the UI.
