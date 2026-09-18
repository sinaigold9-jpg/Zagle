# Explore and public content setup

Run `schema.sql`, then `explore.sql` in Supabase. This migration adds the explicit `profiles.is_discoverable` privacy flag, public suggestions, and public ads.

The Android app queries only public groups, public rooms, profiles explicitly marked discoverable, reviewed suggestions, and enabled public ads. It never queries private conversations or messages for Explore.

Do not add fake rows for production. Curate `explore_suggestions` and `public_ads` through the Supabase dashboard or a protected server-side process. Never expose a service-role key in Android or GitHub.
