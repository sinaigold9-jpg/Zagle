# Zajel — implementation status

Completed stages: 1 through 20.

## Build entry point

The repository includes `gradlew` and `gradlew.bat` launchers. They delegate to a locally installed Gradle 8.x executable and intentionally do not download or commit Gradle binaries. This preserves the repository's no-binaries policy.

### Termux/Ubuntu Noble ARM64

The Android Gradle Plugin version used by this project is 8.5.2. Use Gradle 8.7 with JDK 17.

From the repository root, install the toolchain in the Ubuntu/PRoot environment (not in the Android app and not in the repository):

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk unzip wget ca-certificates
mkdir -p "$HOME/.local/gradle"
wget -O /tmp/gradle-8.7-bin.zip https://services.gradle.org/distributions/gradle-8.7-bin.zip
unzip -q /tmp/gradle-8.7-bin.zip -d "$HOME/.local/gradle"
rm /tmp/gradle-8.7-bin.zip
export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"
export GRADLE_HOME="$HOME/.local/gradle/gradle-8.7"
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"

gradle --version
```

If `sudo` is unavailable in PRoot, run the same commands without `sudo` after installing Java 17 through the Ubuntu environment's package manager, or set `JAVA_HOME` to an existing JDK 17 directory. The Gradle distribution is installed outside the checkout; no binary is added to Git.

Install the Android SDK packages required by this project (API 33 and build tools) using `sdkmanager` from the command-line tools installation:

```bash
sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"
```

Then build:

```bash
chmod +x gradlew
gradle --version
./gradlew clean testDebugUnitTest assembleDebug
./gradlew assembleRelease \
  -PzajelSupabaseUrl=https://your-project.supabase.co \
  -PzajelSupabaseAnonKey=your-public-anon-key
```

The Supabase URL and public anonymous key are supplied locally. Release signing uses a keystore outside Git. Gradle may resolve normal build dependencies, but the project launcher never downloads Gradle or stores binaries in the repository.

The app remains Native Android Java: no WebView, no web redirect for internal sharing, and no mock production data.
