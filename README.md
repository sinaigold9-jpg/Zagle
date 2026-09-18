# Zajel — implementation status

Completed stages: 1 through 20.

## Build entry point

The repository now includes `gradlew` and `gradlew.bat` launchers. They delegate to a locally installed Gradle 8.x executable and do not download or commit Gradle binaries. This is suitable for the documented Termux/Ubuntu ARM64 environment.

### Termux/Ubuntu ARM64

From the repository root:

```bash
chmod +x gradlew
export JAVA_HOME=/opt/jdk-17.0.20.1+1
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
./gradlew clean testDebugUnitTest assembleDebug
./gradlew assembleRelease -PzajelSupabaseUrl=https://your-project.supabase.co -PzajelSupabaseAnonKey=your-public-anon-key
```

The Supabase URL and public anonymous key are supplied locally. Release signing uses a keystore outside GitHub. The launcher does not download dependencies itself; Gradle may resolve normal build dependencies using the configured repositories.

The app remains Native Android Java: no WebView, no web redirect for internal sharing, and no mock production data.
