# Zajel — implementation status

Completed stages: 1 through 20.

## Current integrity fixes

- `ChannelsActivity`, `CommunitiesActivity`, and `PrivacySecurityActivity` are valid Native Android Activities and are registered in `AndroidManifest.xml`.
- The privacy/security screen is now in package `com.zajel.app`; repository code remains in `com.zajel.app.data`.
- Channel and Community screens use repository-backed server data only. No WebView, web redirect, or mock production data was added.
- `AppContainer` remains the single dependency composition root.

## Termux/Ubuntu verification

```bash
export JAVA_HOME=/opt/jdk-17.0.20.1+1
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
./gradlew clean testDebugUnitTest assembleDebug
./gradlew assembleRelease -PzajelSupabaseUrl=https://your-project.supabase.co -PzajelSupabaseAnonKey=your-public-anon-key
```

The Supabase URL and public anonymous key are supplied locally. Keystore/signing files remain outside GitHub.
