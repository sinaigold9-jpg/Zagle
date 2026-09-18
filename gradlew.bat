@echo off
rem Zajel Gradle launcher for Windows hosts.
rem The project intentionally delegates to a locally installed Gradle executable.
if "%JAVA_HOME%"=="" (
  echo JAVA_HOME is not set.
  exit /b 1
)
if "%ANDROID_HOME%"=="" if "%ANDROID_SDK_ROOT%"=="" (
  echo ANDROID_HOME or ANDROID_SDK_ROOT is not set.
  exit /b 1
)
where gradle >nul 2>nul
if errorlevel 1 (
  echo Gradle was not found in PATH. Install a compatible Gradle 8.x binary.
  exit /b 127
)
gradle %*
