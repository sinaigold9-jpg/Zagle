# Zajel Supabase setup

This folder contains the SQL contract for integrating Supabase with the native Android app. It is designed for a local mobile-only workflow and does not include any secret values or signed artifacts.

Files:
- `schema.sql` — table definitions and Row Level Security policies for profiles, conversations, groups, rooms, and storage access contracts.

After creating the project in Supabase:
1. run the SQL in `schema.sql`
2. create a private bucket named `zajel-media`
3. configure Storage RLS so authenticated users can upload/read their own files and the app can fetch only allowed media
4. keep the URL and anon key only in local build settings, not in GitHub
