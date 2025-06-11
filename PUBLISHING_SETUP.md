# Manual Local Publishing Guide (ローカル手動公開ガイド)

このドキュメントは、**ローカル環境から手動でMaven Centralに公開する**場合の設定方法です。

> **📝 注意**: 通常のリリースはGitHub Actionsで自動化されています。このガイドは緊急時やテスト目的での手動公開用です。自動リリースについては [RELEASE_PROCESS.md](RELEASE_PROCESS.md) を参照してください。

## 手動公開が必要な場合

- GitHub Actions が利用できない緊急時
- 新機能のテスト公開
- ローカルでの動作確認

## 必要な認証設定

`~/.gradle/gradle.properties` ファイルに以下を設定してください：

```properties
# Central Portal 認証情報
mavenCentralUsername=your_sonatype_central_username
mavenCentralPassword=your_sonatype_central_password

# GPG 署名設定
signingKey=your_private_key_ascii_format
signingPassword=your_key_password
```

> **⚠️ セキュリティ注意**: これらの認証情報はローカル環境でのみ使用し、リポジトリにコミットしないでください。

## 手動公開手順

### 1. アーティファクトのアップロード

```bash
# バージョンを事前に確認
grep "version = " build.gradle.kts

# Maven Central Portal にアップロード（deploymentを作成）
./gradlew publishAllPublicationsToMavenCentralRepository
```

### 2. Maven Central Portal で手動Publish

上記コマンド実行後、**Maven Central Portal での手動操作が必要**です：

1. **[Central Portal](https://central.sonatype.com/) にログイン**
2. **ナビゲーションの「Publish」をクリック**
3. **作成されたデプロイメントを確認**
   - Status: "VALIDATED"
4. **「Publish」ボタンをクリック**

> **📝 重要**: Gradleコマンドだけではアーティファクトはアップロードされるだけで、実際の公開には Central Portal での手動Publish操作が必要です。

### 3. 公開確認

- 公開後、[Maven Central Search](https://search.maven.org/) で検索可能になるまで数分〜数時間かかります
- `io.github.haradakunihiko:php-json-deserializer-kt` で検索して確認

## 関連ドキュメント

- **自動リリース**: [RELEASE_PROCESS.md](RELEASE_PROCESS.md) - GitHub Actions による自動リリースプロセス
- **CI/CD設定**: GitHub Repository Settings → Secrets で同じ認証情報をシークレットとして設定
