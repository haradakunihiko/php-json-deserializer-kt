#!/bin/bash

# GitHub Release Script
# Usage: ./scripts/release.sh <version>
# Example: ./scripts/release.sh 1.0.1

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

# Update version in build.gradle.kts
echo "📝 Updating version in build.gradle.kts..."
sed -i.bak "s/version = \".*\"/version = \"$VERSION\"/" build.gradle.kts
rm build.gradle.kts.bak

# Run tests to make sure everything works
echo "🧪 Running tests..."
./gradlew test

# Commit version change
echo "💾 Committing version change..."
git add build.gradle.kts
git commit -m "Release version $VERSION"

# Create and push tag
echo "🏷️  Creating tag $TAG..."
git tag -a "$TAG" -m "Release version $VERSION"

echo "📤 Pushing changes and tag to GitHub..."
git push origin main
git push origin "$TAG"

# Create GitHub release
echo "🎉 Creating GitHub release..."
# gh release create "$TAG" \
#     --title "Release $VERSION" \
#     --notes "Release version $VERSION" \
#     --latest

echo "✅ Release $VERSION created successfully!"
echo ""
echo "📋 Next steps:"
echo "1. Check GitHub Actions for the release workflow"
echo "2. Verify the release on GitHub: https://github.com/$(git remote get-url origin | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/releases"
echo "3. Monitor Maven Central for the published artifact"
