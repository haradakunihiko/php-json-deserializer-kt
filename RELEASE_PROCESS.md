# Release Process

This document describes the automated release process for the PHP JSON Deserializer Kotlin library.

## Overview

The release process consists of:

1. **Local script** (`scripts/release.sh`) - Validation and tag creation
2. **GitHub Actions** (`.github/workflows/release.yml`) - Automated release workflow

## How to Release

### 1. Prepare for Release

Ensure you're on the main branch with a clean working directory:

```bash
git checkout main
git pull origin main
git status  # Should be clean
```

### 2. Run the Release Script

```bash
./scripts/release.sh 1.2.1
```

This script will:
- Validate preconditions (clean working directory, on main branch, etc.)
- Run tests to ensure everything works
- Create and push a version tag (e.g., `v1.2.1`)
- Trigger the GitHub Actions workflow

### 3. Automated GitHub Actions Workflow

Once the tag is pushed, GitHub Actions automatically:

1. **Creates a release branch** (`release/v1.2.1`)
2. **Updates version** in `build.gradle.kts`
3. **Runs tests** to ensure quality
4. **Creates a Pull Request** with the version update
5. **Auto-merges the PR** (squash merge)
6. **Creates a GitHub Release** with auto-generated release notes
7. **Publishes to Maven Central**

## Required Secrets Configuration

For the automation to work, configure these secrets in your GitHub repository:

### Repository Settings → Secrets and variables → Actions

| Secret Name | Description | How to Obtain |
|-------------|-------------|---------------|
| `MAVEN_CENTRAL_USERNAME` | Sonatype username | Your Sonatype JIRA username |
| `MAVEN_CENTRAL_PASSWORD` | Sonatype password | Your Sonatype JIRA password |
| `SIGNING_KEY` | GPG private key | Export with `gpg --armor --export-secret-keys KEY_ID` |
| `SIGNING_PASSWORD` | GPG key passphrase | Passphrase used when creating the GPG key |

### Setting up GPG Signing

If you haven't set up GPG signing yet:

1. **Generate a GPG key:**
   ```bash
   gpg --full-generate-key
   ```

2. **Export the private key:**
   ```bash
   gpg --list-secret-keys --keyid-format LONG
   gpg --armor --export-secret-keys YOUR_KEY_ID
   ```

3. **Add the exported key to GitHub Secrets** as `SIGNING_KEY`

## Workflow Details

### GitHub Actions Permissions

The workflow uses the automatically provided `GITHUB_TOKEN` with these permissions:
- `contents: write` - For pushing commits and creating releases
- `pull-requests: write` - For creating and merging PRs


## Monitoring

After running the release script:

1. **Check GitHub Actions**: Go to the Actions tab to monitor progress
2. **Verify the Release**: Check the Releases page for the new release
3. **Monitor Maven Central**: It may take some time for the artifact to appear

## Troubleshooting

### Common Issues

**GitHub Actions fails with permission errors:**
- Ensure the repository has the correct permissions set
- Check if branch protection rules are blocking the workflow

**Maven Central publishing fails:**
- Verify all secrets are correctly configured
- Check that the GPG key is valid and not expired

**PR auto-merge fails:**
- Check if branch protection rules require reviews
- Ensure the workflow has sufficient permissions

### Manual Recovery

If the automated process fails partway through:

1. **Check the failed step** in GitHub Actions logs
2. **Fix the underlying issue** (missing secrets, etc.)
3. **Re-run the workflow** from the Actions tab, or
4. **Push the tag again** after fixing the issue:
   ```bash
   git tag -d v1.2.1  # Delete local tag
   git push origin :v1.2.1  # Delete remote tag
   ./scripts/release.sh 1.2.1  # Try again
   ```
