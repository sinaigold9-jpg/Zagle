# Gradle installation policy

`gradlew` and `gradlew.bat` are repository launchers for a locally installed Gradle executable. This repository intentionally omits `gradle-wrapper.jar` and all other binaries. The launcher does not download Gradle; it checks the environment and delegates to `gradle` on `PATH`.

## Supported toolchain

The Android Gradle Plugin is `8.5.2`. Use **Gradle 8.7** and **JDK 17**. This combination works with Ubuntu Noble ARM64 under Termux/PRoot and keeps the build reproducible without committing a binary distribution.

## Ubuntu Noble ARM64 in Termux/PRoot

Run these commands inside the Ubuntu environment, from any directory:

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk unzip wget ca-certificates
mkdir -p "$HOME/.local/gradle"
wget -O /tmp/gradle-8.7-bin.zip https://services.gradle.org/distributions/gradle-8.7-bin.zip
unzip -q /tmp/gradle-8.7-bin.zip -d "$HOME/.local/gradle"
rm /tmp/gradle-8.7-bin.zip
export JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"
export GRADLE_HOME="$HOME/.local/gradle/gradle-8.7"
export PATH="$JAVA_HOME/bin:$GRADLE_HOME/bin:$PATH"

gradle --version
```

When `sudo` is not available in PRoot, omit it if the package manager is already permitted, or use an existing JDK 17 and set `JAVA_HOME` to its installation directory. Keep `GRADLE_HOME` outside the Git checkout. To make the variables persistent, add the three `export` lines to `~/.bashrc` (or the shell startup file used by the Ubuntu session).

After cloning the repository:

```bash
cd Zagle
chmod +x gradlew
export ANDROID_HOME="$HOME/Android/Sdk"
export ANDROID_SDK_ROOT="$ANDROID_HOME"
export PATH="$ANDROID_HOME/platform-tools:$PATH"
./gradlew --version
./gradlew clean testDebugUnitTest assembleDebug
```

The Android build uses API 33. Ensure the SDK contains it before building:

```bash
sdkmanager --licenses
sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.2"
```

If `gradle` is missing from `PATH`, `gradlew` exits with an actionable message. Do not generate or commit `gradle-wrapper.jar`, downloaded Gradle distributions, APKs, or other binaries.
