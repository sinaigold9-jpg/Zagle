# Phase 2: Home and private chats

Native Android source for Zajel 1.1.0.0. Phase 2 adds only two features: a real Home conversation hub and private text-chat foundations backed by Supabase REST. No WebView, PWA, HTML, JavaScript, fake users, fake conversations, or fake messages are included.

The app reads only authenticated data. An empty account shows “No conversations yet”; it does not create placeholder content.

Expected Supabase tables/RLS:
- `conversations`: `id`, `title`, `is_direct`
- `conversation_members`: `conversation_id`, `user_id`, `created_at`, with a relation named `conversations`
- `messages`: `id`, `conversation_id`, `sender_id`, `body`, `message_type`, `created_at`

The authenticated user must have RLS permissions to read memberships/messages and insert messages. Configure locally with `-PzajelSupabaseUrl` and `-PzajelSupabaseAnonKey`.

Development plan: the specification contains 20 stages. Phase 2 is complete after these two features; 18 stages remain.
