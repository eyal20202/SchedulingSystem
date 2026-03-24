@echo off
REM Script to prepare project for Git submission
REM הכנת הפרויקט להגשה ב-Git

echo 🚀 Preparing Scheduling System for Git submission...
echo.

REM Initialize Git if not already initialized
if not exist ".git" (
    echo 📦 Initializing Git repository...
    git init
    echo ✅ Git initialized
) else (
    echo ✅ Git repository already exists
)

REM Clean build artifacts
echo.
echo 🧹 Cleaning build artifacts...
if exist "backend\target" rmdir /s /q "backend\target"
if exist "frontend\dist" rmdir /s /q "frontend\dist"
if exist "frontend\node_modules" rmdir /s /q "frontend\node_modules"
if exist "backend\node_modules" rmdir /s /q "backend\node_modules"
if exist "docker\node_modules" rmdir /s /q "docker\node_modules"
del /s /q *.log 2>nul
del /s /q *.tsbuildinfo 2>nul
echo ✅ Build artifacts cleaned

REM Add all relevant files
echo.
echo 📝 Adding files to Git...
git add .gitignore
git add README.md
git add INSTALLATION.md
git add API_ENDPOINTS.md
git add backend/
git add frontend/
git add docker/
echo ✅ Files added

REM Show status
echo.
echo 📊 Git Status:
git status

echo.
echo ✅ Project is ready for Git submission!
echo.
echo Next steps:
echo 1. Review the files: git status
echo 2. Commit: git commit -m "Initial commit - Scheduling System"
echo 3. Add remote: git remote add origin ^<your-repo-url^>
echo 4. Push: git push -u origin main
echo.
pause
