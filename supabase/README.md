# Zajel — phases 12 and 13

Implemented directly in native Java:

- Phase 12: user-selected backup export/import through Android Storage Access Framework, compatible with Google Drive and other document providers. Backup selection is explicit and no offline automatic Drive sync is claimed.
- Phase 13: local received-file manager with document picker, private app storage, listing, and native sharing.

Run `supabase/phase12_phase13.sql` after the previous Supabase migrations. The tables store backup preferences and file metadata only; the actual backup/file bytes remain in the user-selected provider or private device storage.
