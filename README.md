# Zajel — Native authentication

The launcher now opens a native Arabic-friendly authentication screen. Registration requires a first name, family name, email or phone number, password, and matching password confirmation. Existing users can sign in with the same email or phone method they selected at registration.

Run `supabase/schema.sql`, then `supabase/auth_profile.sql`, then `supabase/explore.sql`. Supabase email/phone confirmation settings remain controlled by the Supabase project dashboard.

No passwords, tokens, service-role keys, or fake accounts are stored in the repository.
