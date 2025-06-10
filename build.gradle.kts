import org.gradle.plugins.signing.SigningExtension

plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    id("org.jetbrains.dokka") version "1.9.10"
    id("jacoco")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "io.github.haradakunihiko"
version = "1.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.21")
    
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

kotlin {
    // jvmToolchain(11) // 一時的にコメントアウト
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}


// タスクの依存関係を修正
afterEvaluate {
    tasks.named("generateMetadataFileForMavenPublication") {
        dependsOn("dokkaJavadocJar", "kotlinSourcesJar")
    }
}




// Central Portal設定
mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    
    // 署名設定が利用可能な場合のみ署名を有効化
    val signingRequired = project.findProperty("signing.required")?.toString()?.toBoolean() ?: true
    if (signingRequired) {
        signAllPublications()
        
        // 署名の設定
        configure<SigningExtension> {
            val signingKey: String? by project
            val signingPassword: String? by project
            if (signingKey != null && signingPassword != null) {
                useInMemoryPgpKeys(signingKey, signingPassword)
            }
        }
    }
    
    pom {
        name.set("PHP JSON Deserializer Kotlin")
        description.set("Kotlin library for deserializing PHP serialized data to JSON (bd808/php-unserialize-js compatible)")
        url.set("https://github.com/haradakunihiko/php-json-deserializer-kt")
        
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        
        developers {
            developer {
                id.set("haradakunihiko")
                name.set("Kunihiko Harada")
                email.set("sthsoulful@gmail.com")
            }
        }
        
        scm {
            connection.set("scm:git:git://github.com/haradakunihiko/php-json-deserializer-kt.git")
            developerConnection.set("scm:git:ssh://github.com/haradakunihiko/php-json-deserializer-kt.git")
            url.set("https://github.com/haradakunihiko/php-json-deserializer-kt")
        }
    }
}
