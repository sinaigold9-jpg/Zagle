# Gradle launcher policy

`gradlew` and `gradlew.bat` are repository launchers for a locally installed Gradle 8.x executable. They intentionally do not download Gradle or commit binary wrapper JARs. This keeps the public repository small and avoids placing executable binaries in GitHub.

On a fresh Termux/Ubuntu checkout, run once:

```bash
chmod +x gradlew
export JAVA_HOME=/opt/jdk-17.0.20.1+1
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
gradle --version
```

Then the documented `./gradlew ...` commands work through the local Gradle installation. If `gradle` is not installed, install a compatible Gradle 8.x release in Ubuntu/Termux; the launcher intentionally fails with a clear message instead of downloading anything.
