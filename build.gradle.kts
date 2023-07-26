import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    application
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

group = "com.trueddd.github"
version = "0.0.1"

application {
    mainClass.set("com.github.trueddd.ApplicationKt")
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_16
    }
    sourceSets.main {
        kotlin.srcDirs(file("$buildDir/generated/ksp/main/kotlin"))
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":di"))
    ksp(project(":di"))
    implementation(libs.logging)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.headers.default)
    implementation(libs.ktor.server.headers.conditional)
    implementation(libs.ktor.server.auth.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.serialization)
    implementation(libs.koin.ktor)
    ksp(libs.koin.ksp)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.test.kotlin)
    testImplementation(libs.test.jupiter.engine)
    testImplementation(libs.test.jupiter.api)
    testImplementation(libs.coroutines.test)
}
