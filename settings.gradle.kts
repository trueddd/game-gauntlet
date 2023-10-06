rootProject.name = "game-gauntlet"
include("annotations", "di", "shared")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("multiplatform") version "1.9.0"
        kotlin("plugin.serialization") version "1.9.0"
        id("io.ktor.plugin") version "2.3.2"
        id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
        id("com.google.devtools.ksp") version "1.9.0-1.0.12"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
    }
    versionCatalogs {
        create("libs") {
            val kotlin = version("kotlin", "1.9.0")
            val ksp = version("ksp", "1.9.0-1.0.12")
            val ktor = version("ktor", "2.3.2")
            val jupiter = version("jupiter", "5.10.0")

            library("logging", "ch.qos.logback", "logback-classic").version("1.4.8")

            plugin("ktor", "io.ktor.plugin").versionRef(ktor)
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef(ktor)
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef(ktor)
            library("ktor-server-websockets", "io.ktor", "ktor-server-websockets").versionRef(ktor)
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef(ktor)
            library("ktor-server-call-logging", "io.ktor", "ktor-server-call-logging").versionRef(ktor)
            library("ktor-server-headers-default", "io.ktor", "ktor-server-default-headers").versionRef(ktor)
            library("ktor-server-headers-conditional", "io.ktor", "ktor-server-conditional-headers").versionRef(ktor)
            library("ktor-server-cors", "io.ktor", "ktor-server-cors").versionRef(ktor)
            library("ktor-server-auth-core", "io.ktor", "ktor-server-auth").versionRef(ktor)
            library("ktor-server-auth-jwt", "io.ktor", "ktor-server-auth-jwt").versionRef(ktor)
            library("ktor-server-tests", "io.ktor", "ktor-server-tests").versionRef(ktor)
            library("ktor-serialization", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)

            library("test-kotlin", "org.jetbrains.kotlin", "kotlin-test-junit").versionRef(kotlin)
            library("test-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(jupiter)
            library("test-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(jupiter)

            library("koin-core", "io.insert-koin", "koin-core").version("3.4.2")
            library("koin-ktor", "io.insert-koin", "koin-ktor").version("3.4.1")
            library("koin-annotations", "io.insert-koin", "koin-annotations").version("1.2.2")
            library("koin-ksp", "io.insert-koin", "koin-ksp-compiler").version("1.2.2")

            plugin("ksp", "com.google.devtools.ksp").versionRef(ksp)
            library("kotlin-poet", "com.squareup", "kotlinpoet").version("1.14.2")
            library("kotlin-ksp-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef(ksp)

            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef(kotlin)
            library("serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.6.0")

            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").version("1.7.1")
        }
    }
}
