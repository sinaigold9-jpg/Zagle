# Phase 1: Accounts, users, profile, and basic settings

Native Android source for Zajel 1.1.0.0. No WebView, PWA, HTML, JavaScript, secrets, signing files, or fake production data are included.

Configure Supabase locally with `-PzajelSupabaseUrl` and `-PzajelSupabaseAnonKey`. The `profiles` table must expose `id`, `username`, `display_name`, and `avatar_url` with suitable RLS policies.
