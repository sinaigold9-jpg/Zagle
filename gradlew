#!/usr/bin/env sh
# Zajel Gradle launcher for Termux/Ubuntu ARM64.
# This repository intentionally does not download Gradle or store binaries.
# It delegates to a locally installed Gradle executable.
set -eu

if [ -z "${JAVA_HOME:-}" ]; then
  echo "JAVA_HOME is not set. Example: export JAVA_HOME=/opt/jdk-17.0.20.1+1" >&2
  exit 1
fi

if [ ! -x "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME does not contain a runnable JDK: $JAVA_HOME" >&2
  exit 1
fi

if [ -z "${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}" ]; then
  echo "ANDROID_HOME or ANDROID_SDK_ROOT is not set. Example: export ANDROID_HOME=/opt/android-sdk" >&2
  exit 1
fi

if [ -z "${ANDROID_HOME:-}" ]; then
  export ANDROID_HOME="$ANDROID_SDK_ROOT"
fi
if [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  export ANDROID_SDK_ROOT="$ANDROID_HOME"
fi

if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi

cat >&2 <<'EOF'
Gradle was not found in PATH.
Install a compatible Gradle 8.x binary in Ubuntu/Termux, then run this command again.
This launcher does not download binaries or store them in the repository.
EOF
exit 127
