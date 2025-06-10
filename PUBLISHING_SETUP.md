# Maven Central Portal 公開ガイド

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

## 公開コマンド

```bash
./gradlew publishAllPublicationsToMavenCentralRepository
```
