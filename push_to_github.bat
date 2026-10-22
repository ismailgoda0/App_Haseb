@echo off
title HomeCash - Push to GitHub

echo ======================================================
echo           HomeCash - Push to GitHub
echo ======================================================
echo.

where git >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Git is not installed or not in your PATH.
    echo Please install Git from: https://git-scm.com/
    goto PAUSE_END
)

echo [*] Staging changed files (git add .)...
git add .

echo.
set "COMMIT_MSG="
set /p COMMIT_MSG="Enter commit message (Press Enter for auto timestamp): "
if not defined COMMIT_MSG (
    set "COMMIT_MSG=Update HomeCash project"
)

echo [*] Committing changes...
git commit -m "%COMMIT_MSG%"

echo.
echo [*] Syncing with remote repository...
git pull origin main --no-rebase --no-edit >nul 2>nul

echo [*] Pushing to GitHub (origin main)...
git push origin main

if %errorlevel% equ 0 (
    echo.
    echo ======================================================
    echo          SUCCESS: Uploaded to GitHub!
    echo   https://github.com/ismailgoda0/App_Haseb
    echo ======================================================
) else (
    echo.
    echo ======================================================
    echo [WARNING] Push failed. Check your internet connection.
    echo ======================================================
)

:PAUSE_END
echo.
pause
