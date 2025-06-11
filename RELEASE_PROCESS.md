# Release Process

This document describes the automated release process for the PHP JSON Deserializer Kotlin library.

## Overview

The release process consists of:

1. **Manual trigger** (GitHub Actions Web UI) - Start release workflow
2. **GitHub Actions workflows** - Automated release, tagging, and publishing

## How to Release

### 1. Start Release (GitHub Web UI)

1. **Navigate to GitHub Actions**
   - Go to the repository on GitHub
   - Click on the "Actions" tab
   - Select "Start Release" workflow

2. **Run the workflow**
   - Click "Run workflow"
   - Enter the release version (e.g., `1.3.0`)
   - Click "Run workflow" button

### 2. Automated PR Creation

The "Start Release" workflow automatically:

1. **Validates version format** (x.y.z)
2. **Checks if version already exists**
3. **Creates release branch** (`release/v1.3.0`)
4. **Updates version** in `build.gradle.kts`
5. **Runs tests** to ensure quality
6. **Creates a Pull Request** with the version update

### 3. Manual Review and Merge

After the automated PR is created:

1. **Review the PR** - Check the version update and any other changes
2. **Verify CI checks pass** - Ensure all tests and quality checks succeed
3. **Manually merge the PR** - Use GitHub's web interface to merge when ready

### 4. Automated Tag and Release Draft Creation

Once the PR is merged, the "Release" workflow automatically:

1. **Creates Git tag** at the correct merge commit
2. **Creates GitHub Release draft** with auto-generated release notes
3. **Cleans up the release branch**

### 5. Manual Release Review and Publishing

After the release draft is created:

1. **Review the release draft** - Go to GitHub Releases page
2. **Edit release notes** if needed (optional)
3. **Publish the release** manually when ready

### 6. Automated Maven Central Upload

Once the GitHub Release is published, the "Publish to Maven Central" workflow automatically:

1. **Runs tests** to ensure quality
2. **Uploads artifact to Maven Central Portal** (creates deployment)
3. **Provides next steps instructions**

### 7. Manual Maven Central Publishing

After the GitHub Actions workflow completes, **manual action is required**:

1. **Go to [Maven Central Portal](https://central.sonatype.com/)**
2. **Log in** with your Maven Central credentials
3. **Navigate to "Publish"** in the navigation menu
4. **Find your deployment** (Status: "VALIDATED")
5. **Click "Publish"** button

> **⚠️ Important**: The release is not complete until you manually publish the deployment in Maven Central Portal.

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

**PR cannot be merged:**
- Check if all CI checks have passed
- Verify there are no merge conflicts
- Ensure branch protection rules are satisfied

### Manual Recovery

If the automated process fails partway through:

1. **Check the failed step** in GitHub Actions logs
2. **Fix the underlying issue** (missing secrets, etc.)
3. **Re-run the workflow** from the Actions tab

**If release PR creation failed:**
- Re-run the "Start Release" workflow with the same version
- The workflow will detect and handle existing branches appropriately

**If tag/release creation failed:**
- Check if the PR was merged successfully
- Re-run the "Release" workflow manually if needed

**If Maven Central publishing failed:**
- Check the GitHub release was created successfully
- Re-run the "Publish to Maven Central" workflow manually
- Verify all required secrets are configured correctly

**Emergency cleanup:**
If you need to completely restart a release:
```bash
# Delete the release branch if it exists
git push origin --delete release/v1.3.0

# Delete the tag if it was created
git tag -d v1.3.0
git push origin :v1.3.0

# Then restart the release process from GitHub Actions
```
