plugins {
    application
    kotlin("jvm") version Versions.Kotlin
    id("io.ktor.plugin") version Versions.Ktor
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.Kotlin
}

group = Config.PackageName
version = Config.Version

application {
    mainClass.set("com.github.trueddd.ApplicationKt")
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
}

dependencies {
    implementation(Dependency.Logging)
    implementation(Dependency.Ktor.Server.Core)
    implementation(Dependency.Ktor.Server.Netty)
    implementation(Dependency.Ktor.Server.WebSockets)
    implementation(Dependency.Ktor.Server.ContentNegotiation)
    implementation(Dependency.Ktor.Server.CallLogging)
    implementation(Dependency.Ktor.Server.Cors)
    implementation(Dependency.Ktor.Server.DefaultHeaders)
    implementation(Dependency.Ktor.Server.ConditionalHeaders)
    implementation(Dependency.Ktor.Server.Auth)
    implementation(Dependency.Ktor.Server.AuthJwt)
    implementation(Dependency.Ktor.Serialization)
    implementation(Dependency.Ktor.Server.HtmlBuilder)
    implementation(Dependency.HtmlJvm)
    implementation(Dependency.CssJvm)
    testImplementation(Dependency.Ktor.Server.Tests)
    testImplementation(Dependency.Junit)
}