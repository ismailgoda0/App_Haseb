@echo off
chcp 65001 >nul
title HomeCash - Push to GitHub

echo ======================================================
echo          HomeCash - رفع التحديثات إلى GitHub
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
echo [*] تتبع الملفات الجديدة والمعدلة (git add .)...
git add .

echo.
set COMMIT_MSG=
set /p COMMIT_MSG="اكتب وصف التعديل (أو اضغط Enter لرسالة تلقائية): "
if "%COMMIT_MSG%"=="" set COMMIT_MSG=Update HomeCash project

echo [*] جاري حفظ التعديل (git commit)...
git commit -m "%COMMIT_MSG%"

echo.
echo [*] جاري سحب أي تحديثات جديدة من GitHub قبل الرفع...
git pull origin main --no-rebase --no-edit >nul 2>nul

echo.
echo [*] جاري الرفع إلى GitHub (origin main)...
git push origin main

if errorlevel 1 goto PUSH_FAILED

echo.
echo ======================================================
echo          تم رفع المشروع بنجاح إلى GitHub!
echo   https://github.com/ismailgoda0/App_Haseb
echo ======================================================
goto END

:PUSH_FAILED
echo.
echo ======================================================
echo [تنبيه] حدث خطأ أثناء الرفع!
echo تأكد من اتصال الإنترنت وصلاحيات حسابك على GitHub.
echo ======================================================
goto END

:NO_GIT
echo [خطأ] بروتوكول Git غير مثبت على جهازك أو غير مضاف إلى PATH.
echo يرجى تحميله من: https://git-scm.com/
goto END

:END
echo.
pause
