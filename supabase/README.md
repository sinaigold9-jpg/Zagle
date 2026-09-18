# Explore and public content setup

Run `schema.sql`, then `explore.sql` in Supabase. The app reads public groups, public rooms, intentionally discoverable profiles, reviewed suggestions, and enabled public ads only.

Do not add fake rows for testing production. Use Supabase's dashboard or a protected server-side process to curate `explore_suggestions` and `public_ads`. Never expose a service-role key in Android or GitHub.
