# Zajel — Explore, Search, Suggestions, and Public Ads

Native Android Java source only. This revision implements exactly:

- Phase 9: Explore plus scalable public search for users, public groups, and public rooms.
- Phase 10: public suggestions and public notices/ads, isolated from private chat data.

Search never queries private conversations or private messages. Suggestions and ads are read from explicit public tables only. Empty server results remain empty states; no fake data is generated.

## Supabase schema
Run `supabase/schema.sql` in the Supabase SQL editor. Keep the project URL and public anon key in local Gradle properties only:

```sh
./gradlew assembleDebug -PzajelSupabaseUrl=https://YOUR_PROJECT.supabase.co -PzajelSupabaseAnonKey=YOUR_PUBLIC_ANON_KEY
```

No service-role keys, passwords, tokens, keystores, APKs, or AABs belong in this repository.

## Review notes
- Authentication uses the existing explicit `signIn`/`signUp` contract.
- Explore is behind a repository boundary and network work runs off the UI thread.
- Public search is limited to public groups/rooms and profile fields intended for discovery.
- Private chat, media, group, and room repositories remain separate from Explore.
- The source remains compatible with Termux + Ubuntu ARM64 + JDK 17 and requires no Android Studio or WebView.

Completed stages: 1, 2, 3, 4, 5, 8, 9, 10. Remaining stages: 12 (6, 7, 11–20).
