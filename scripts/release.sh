#!/bin/bash

# GitHub Release Script (Minimal version)
# Usage: ./scripts/release.sh <version>
# Example: ./scripts/release.sh 1.0.1
#
# This script only creates and pushes a tag.
# The actual release process is handled by GitHub Actions.

set -e

# Check if version is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <version>"
    echo "Example: $0 1.0.1"
    exit 1
fi

VERSION=$1
TAG="v$VERSION"

echo "🚀 Starting release process for version $VERSION"

# Check if we're on main branch
CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "❌ Please switch to main branch before creating a release"
    echo "Current branch: $CURRENT_BRANCH"
    exit 1
fi

# Check if working directory is clean
if [ -n "$(git status --porcelain)" ]; then
    echo "❌ Working directory is not clean. Please commit or stash changes first."
    git status --short
    exit 1
fi

# Pull latest changes
echo "📥 Pulling latest changes from origin/main..."
git pull origin main

# Check if tag already exists
if git tag -l | grep -q "^$TAG$"; then
    echo "❌ Tag $TAG already exists"
    exit 1
fi

# Run tests to make sure everything works before tagging
echo "🧪 Running tests..."
./gradlew test

# Create and push tag (this will trigger GitHub Actions)
echo "🏷️  Creating tag $TAG..."
git tag -a "$TAG" -m "Release version $VERSION"

echo "📤 Pushing tag to GitHub..."
git push origin "$TAG"

echo "✅ Tag $TAG pushed successfully!"
echo ""
echo "📋 The release process will continue automatically via GitHub Actions:"
echo "1. Version will be updated in build.gradle.kts via PR"
echo "2. GitHub release will be created with auto-generated notes"
echo "3. Artifact will be published to Maven Central"
echo ""
echo "🔗 Monitor progress at: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/actions"
