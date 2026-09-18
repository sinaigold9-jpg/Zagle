# Zajel — phases 11 and 14

Implemented natively in Java:

- Phase 11: notification list, Android notification channel, and user-controlled notification settings.
- Phase 14: runtime-protected device contacts screen and native invitation sharing. Contacts are not uploaded to Supabase.

Run `supabase/phase11_phase14.sql` after the existing schema migrations. The application requests `READ_CONTACTS` only when the contacts screen is opened and requests no contact permission at startup. Notification delivery can be connected to the existing `notifications` table or a protected server-side event pipeline.
