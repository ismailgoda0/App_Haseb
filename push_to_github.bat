@echo off
chcp 65001 >nul
title HomeCash - Push to GitHub

echo ======================================================
echo          HomeCash - Git Push to GitHub
echo ======================================================
echo.

where git >nul 2>nul
if errorlevel 1 goto NO_GIT

if not exist ".git" goto INIT_GIT
goto CHECK_REMOTE

:INIT_GIT
echo [*] Initializing local git repository...
git init
git branch -M main

:CHECK_REMOTE
set REPO_URL=https://github.com/ismailgoda0/App_Haseb.git

git remote get-url origin >nul 2>nul
if errorlevel 1 goto ADD_REMOTE

git remote set-url origin %REPO_URL%
goto STAGE_FILES

:ADD_REMOTE
echo [*] Setting remote origin to %REPO_URL%
git remote add origin %REPO_URL%

:STAGE_FILES
git branch -M main
echo [*] Adding files to git...
git add .

echo.
set COMMIT_MSG=
set /p COMMIT_MSG="Enter commit message (or press Enter for default): "
if "%COMMIT_MSG%"=="" set COMMIT_MSG=Update HomeCash project

echo [*] Committing changes...
git commit -m "%COMMIT_MSG%"

echo.
echo [*] Pushing to GitHub (origin main)...
git push -u origin main

if errorlevel 1 goto PUSH_FAILED

echo.
echo ======================================================
echo             SUCCESSFULLY PUSHED TO GITHUB!
echo ======================================================
goto END

:PUSH_FAILED
echo.
echo ======================================================
echo [NOTE] Push failed. Possible reasons:
echo 1. You may need to authenticate with your GitHub account.
echo 2. If remote repository already has files (README, etc.),
echo    run: git pull origin main --allow-unrelated-histories
echo ======================================================
goto END

:NO_GIT
echo [ERROR] Git is not installed or not in your PATH.
echo Please install Git from: https://git-scm.com/
goto END

:END
echo.
pause
