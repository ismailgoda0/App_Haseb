@echo off
setlocal EnableExtensions
set "PROJECT_DIR=%~dp0"
set "GRADLE_VERSION=9.3.1"
if not defined GRADLE_USER_HOME set "GRADLE_USER_HOME=%USERPROFILE%\.gradle"
set "CACHE_ROOT=%GRADLE_USER_HOME%\homecash-distributions"
set "GRADLE_EXE=%CACHE_ROOT%\gradle-%GRADLE_VERSION%\bin\gradle.bat"

if not exist "%GRADLE_EXE%" (
  echo [HomeCash] Gradle %GRADLE_VERSION% is not cached. Downloading the official distribution...
  powershell.exe -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%PROJECT_DIR%tools\bootstrap-gradle.ps1" -Version %GRADLE_VERSION%
  if errorlevel 1 exit /b 1
)
if not exist "%GRADLE_EXE%" (
  echo [HomeCash] ERROR: Gradle executable was not found after bootstrap.
  exit /b 1
)
pushd "%PROJECT_DIR%"
call "%GRADLE_EXE%" %*
set "GRADLE_EXIT=%ERRORLEVEL%"
popd
exit /b %GRADLE_EXIT%
