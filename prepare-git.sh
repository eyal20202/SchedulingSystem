#!/bin/bash

# Script to prepare project for Git submission
# הכנת הפרויקט להגשה ב-Git

echo "🚀 Preparing Scheduling System for Git submission..."
echo ""

# Initialize Git if not already initialized
if [ ! -d ".git" ]; then
    echo "📦 Initializing Git repository..."
    git init
    echo "✅ Git initialized"
else
    echo "✅ Git repository already exists"
fi

# Clean build artifacts
echo ""
echo "🧹 Cleaning build artifacts..."
rm -rf backend/target
rm -rf frontend/dist
rm -rf frontend/node_modules
rm -rf backend/node_modules
rm -rf docker/node_modules
find . -name "*.log" -type f -delete
find . -name "*.tsbuildinfo" -type f -delete
echo "✅ Build artifacts cleaned"

# Add all relevant files
echo ""
echo "📝 Adding files to Git..."
git add .gitignore
git add README.md
git add INSTALLATION.md
git add API_ENDPOINTS.md
git add backend/
git add frontend/
git add docker/
echo "✅ Files added"

# Show status
echo ""
echo "📊 Git Status:"
git status

echo ""
echo "✅ Project is ready for Git submission!"
echo ""
echo "Next steps:"
echo "1. Review the files: git status"
echo "2. Commit: git commit -m 'Initial commit - Scheduling System'"
echo "3. Add remote: git remote add origin <your-repo-url>"
echo "4. Push: git push -u origin main"
echo ""
