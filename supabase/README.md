## إصلاح ملاحظة Gradle Wrapper

تمت إضافة:

- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/README.md`

هذه الملفات تعمل كـ launchers متوافقة مع بيئة Termux/Ubuntu، وتستدعي Gradle 8.x المثبت محليًا. لا يتم تنزيل Gradle تلقائيًا ولا يتم تخزين binary داخل المستودع العام.

### التشغيل في Ubuntu داخل Termux

نفّذ مرة واحدة بعد تنزيل المشروع:

```bash
chmod +x gradlew
export JAVA_HOME=/opt/jdk-17.0.20.1+1
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$PATH"
gradle --version
```

ثم:

```bash
./gradlew clean testDebugUnitTest assembleDebug
./gradlew assembleRelease \\
  -PzajelSupabaseUrl=https://your-project.supabase.co \\
  -PzajelSupabaseAnonKey=your-public-anon-key
```

إذا لم يكن Gradle موجودًا في `PATH`، سيظهر خطأ واضح بدل تنزيل ملف تنفيذي أو إضافة أسرار للمستودع. يجب تثبيت Gradle 8.x محليًا في Ubuntu/Termux قبل البناء. يبقى الـ Keystore خارج GitHub.
