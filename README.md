# Zajel — implementation status

Completed stages: 1 through 20.

## Validation status

Stages 19 and 20 add lightweight domain checks, Native-only architecture validation, release configuration guidance, and a corrected dependency graph. The repository is Android Native Java with no WebView source or mock production data.

### Termux/Ubuntu verification

From Ubuntu ARM64 in Termux:

```bash
export JAVA_HOME=/opt/jdk-17.0.20.1+1
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
./gradlew testDebugUnitTest assembleDebug
./gradlew assembleRelease -PzajelSupabaseUrl=https://your-project.supabase.co -PzajelSupabaseAnonKey=your-public-anon-key
```

If a Gradle wrapper is not present, install/use a compatible Gradle 8.7+ binary or add the wrapper with the Android SDK environment available. Secrets and signing files remain outside Git.
