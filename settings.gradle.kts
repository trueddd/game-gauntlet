rootProject.name = "game-gauntlet"
include("annotations", "di", "common", "backend", "web", "map")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("multiplatform") version "2.0.0"
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
    versionCatalogs {
        create("libs") {
            val kotlin = version("kotlin", "2.0.0")
            val datetime = version("datetime", "0.6.0")
            val coroutines = version("coroutines", "1.8.0")
            val ksp = version("ksp", "2.0.0-1.0.21")
            val ktor = version("ktor", "2.3.12")
            val jupiter = version("jupiter", "5.10.0")
            val compose = version("compose", "1.6.11")
            val koin = version("koin", "3.6.0-wasm-alpha2")
            val koinKsp = version("koin-ksp", "1.3.1")
            val kotlinPoet = version("kotlinpoet", "1.16.0")
            val serialization = version("serialization", "1.7.1")
            val coil = version("coil", "3.0.0-alpha07")
            val detekt = version("detekt", "1.23.6")

            library("logging", "ch.qos.logback", "logback-classic").version("1.5.6")

            plugin("ktor", "io.ktor.plugin").versionRef(ktor)
            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktor)
            library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef(ktor)
            library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef(ktor)
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(ktor)
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef(ktor)
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef(ktor)
            library("ktor-server-websockets", "io.ktor", "ktor-server-websockets").versionRef(ktor)
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef(ktor)
            library("ktor-server-call-logging", "io.ktor", "ktor-server-call-logging").versionRef(ktor)
            library("ktor-server-headers-default", "io.ktor", "ktor-server-default-headers").versionRef(ktor)
            library("ktor-server-headers-conditional", "io.ktor", "ktor-server-conditional-headers").versionRef(ktor)
            library("ktor-server-cors", "io.ktor", "ktor-server-cors").versionRef(ktor)
            library("ktor-server-partial", "io.ktor", "ktor-server-partial-content").versionRef(ktor)
            library("ktor-server-autohead", "io.ktor", "ktor-server-auto-head-response").versionRef(ktor)
            library("ktor-server-auth-core", "io.ktor", "ktor-server-auth").versionRef(ktor)
            library("ktor-server-auth-jwt", "io.ktor", "ktor-server-auth-jwt").versionRef(ktor)
            library("ktor-server-caching", "io.ktor", "ktor-server-caching-headers").versionRef(ktor)
            library("ktor-server-tests", "io.ktor", "ktor-server-test-host").versionRef(ktor)
            library("ktor-serialization", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)

            library("test-kotlin", "org.jetbrains.kotlin", "kotlin-test").versionRef(kotlin)
            library("test-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(jupiter)
            library("test-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(jupiter)

            library("koin-core", "io.insert-koin", "koin-core").versionRef(koin)
            library("koin-ktor", "io.insert-koin", "koin-ktor").versionRef(koin)
            library("koin-annotations", "io.insert-koin", "koin-annotations").versionRef(koinKsp)
            library("koin-ksp", "io.insert-koin", "koin-ksp-compiler").versionRef(koinKsp)

            plugin("ksp", "com.google.devtools.ksp").versionRef(ksp)
            library("kotlin-immutable", "org.jetbrains.kotlinx", "kotlinx-collections-immutable").version("0.3.7")
            library("kotlin-poet", "com.squareup", "kotlinpoet").versionRef(kotlinPoet)
            library("kotlin-ksp-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef(ksp)

            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef(kotlin)
            library("serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef(serialization)

            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").versionRef(coroutines)

            plugin("compose", "org.jetbrains.compose").versionRef(compose)
            plugin("compose-compiler", "org.jetbrains.kotlin.plugin.compose").versionRef(kotlin)
            library("compose-runtime", "org.jetbrains.compose.runtime", "runtime").versionRef(compose)
            library("compose-foundation", "org.jetbrains.compose.foundation", "foundation").versionRef(compose)
            library("compose-material", "org.jetbrains.compose.material3", "material3").versionRef(compose)

            library("uuid", "com.benasher44", "uuid").version("0.8.2")
            library("datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").versionRef(datetime)

            library("mongodb-driver", "org.mongodb", "mongodb-driver-kotlin-coroutine").version("5.1.0")

            library("coil-core", "io.coil-kt.coil3", "coil-core").versionRef(coil)
            library("coil-compose", "io.coil-kt.coil3", "coil-compose").versionRef(coil)
            library("coil-network", "io.coil-kt.coil3", "coil-network-ktor").versionRef(coil)

            plugin("detekt", "io.gitlab.arturbosch.detekt").versionRef(detekt)
        }
    }
}
