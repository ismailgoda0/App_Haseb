@echo off
chcp 65001 > nul
setlocal EnableExtensions
 title HomeCash - Android Studio helper

set "SDK=%ANDROID_SDK_ROOT%"
if not defined SDK set "SDK=%ANDROID_HOME%"
if not defined SDK if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" set "SDK=%LOCALAPPDATA%\Android\Sdk"
if not defined SDK (
  echo Android SDK not found. Open the project in Android Studio and set ANDROID_HOME or ANDROID_SDK_ROOT.
  pause
  exit /b 1
)
set "ADB=%SDK%\platform-tools\adb.exe"
set "APK=%~dp0app\build\outputs\apk\debug\app-debug.apk"
if not exist "%ADB%" (
  echo adb.exe not found at "%ADB%". Install Android SDK Platform-Tools in Android Studio.
  pause
  exit /b 1
)

echo [1/3] Building HomeCash debug APK...
call "%~dp0gradlew.bat" :app:assembleDebug
if errorlevel 1 (
  echo Build failed. Review the Gradle output above.
  pause
  exit /b 1
)
if not exist "%APK%" (
  echo APK was not created at "%APK%".
  pause
  exit /b 1
)

echo.
echo Connected Android devices:
"%ADB%" devices
set /p DEVICE="Enter the device serial to install on (leave blank to cancel): "
if not defined DEVICE exit /b 0

echo [2/3] Installing APK without clearing app data...
"%ADB%" -s "%DEVICE%" install -r "%APK%"
if errorlevel 1 (
  echo Installation failed. Check device authorization and Android version.
  pause
  exit /b 1
)
echo [3/3] Launching HomeCash...
"%ADB%" -s "%DEVICE%" shell am start -n com.homecash/.MainActivity
if errorlevel 1 (
  echo Could not launch HomeCash.
  pause
  exit /b 1
)
echo Done.
pause
