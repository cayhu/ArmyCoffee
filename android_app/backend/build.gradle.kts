plugins {
    kotlin("jvm")
    application
    id("io.ktor.plugin") version "2.3.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.army.coffee"
version = "0.0.1"

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-server-cors:2.3.5")
    implementation("io.ktor:ktor-server-status-pages:2.3.5")

    // Ktor Client for OpenAI calls
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-client-serialization:2.3.5")
    implementation("io.ktor:ktor-client-logging:2.3.5")

    // Email Service
    implementation("com.sun.mail:javax.mail:1.6.2")

    // Firebase Admin SDK
    implementation("com.google.firebase:firebase-admin:9.2.0")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("ch.qos.logback:logback-classic:1.4.11")
}

application {
    mainClass.set("com.army.coffee.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("backend-all.jar")
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveFileName.set("backend-all.jar")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "17"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
