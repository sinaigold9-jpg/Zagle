# Zajel — implementation status

Zajel is a native Android Java application. The first four implementation/fix rounds are present in the repository: the earlier Java/build corrections are included, the dedicated activities are registered and reachable from `MainActivity`, `MainActivity` acts as the home navigation hub, and Supabase requests have centralized connectivity checks with bounded retries.

## Current status

- The app is native Android Java and does not use WebView for its internal screens.
- Authentication starts in `AuthActivity`; authenticated users enter `MainActivity`.
- The home screen links to Explore, Contacts, Notifications, Channels, Communities, Privacy & Security, Backup, and File manager.
- Network requests report offline conditions and retry transient failures a limited number of times.
- JVM architecture checks are real JUnit 4 tests and run through `testDebugUnitTest`.

## Known limitations

- The invitation URL is intentionally a placeholder in `InviteConfig.ZAJEL_INVITE_URL`; it must be replaced with the production Play Store or invitation URL before release.
- The project still requires a local Android SDK, JDK 17, Gradle 8.7, and Supabase build properties for a complete build and backend integration.
- Some feature screens and backend flows remain functional implementations that need end-to-end device/backend validation, including permissions, file sharing, notifications, and Supabase policy configuration.

## Build and test

Use Android API 33, JDK 17, and Gradle 8.7. From the repository root:

```bash
./gradlew clean testDebugUnitTest assembleDebug
```

For a configured release build:

```bash
./gradlew assembleRelease \\
  -PzajelSupabaseUrl=https://your-project.supabase.co \\
  -PzajelSupabaseAnonKey=your-public-anon-key
```

Supabase credentials are supplied locally and must not be committed.
