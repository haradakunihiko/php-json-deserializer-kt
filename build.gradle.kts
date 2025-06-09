plugins {
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.serialization") version "1.9.21"
    id("org.jetbrains.dokka") version "1.9.10"
    id("jacoco")
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

group = "com.github.haradakunihiko"
version = "1.0.0-SNAPSHOT"

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

java {
    withJavadocJar()
    withSourcesJar()
}

// Maven Central公開設定
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
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
                        email.set("haradakunihiko@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/haradakunihiko/php-json-deserializer-kt.git")
                    developerConnection.set("scm:git:ssh://github.com/haradakunihiko/php-json-deserializer-kt.git")
                    url.set("https://github.com/haradakunihiko/php-json-deserializer-kt")
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}
