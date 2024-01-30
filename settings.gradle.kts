rootProject.name = "game-gauntlet"
include("annotations", "di", "common", "backend", "web")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        kotlin("multiplatform") version "1.9.20"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers") }
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental") }
    }
    versionCatalogs {
        create("libs") {
            val kotlin = version("kotlin", "1.9.20")
            val ksp = version("ksp", "1.9.20-1.0.14")
            val ktor = version("ktor", "2.3.7")
            val jupiter = version("jupiter", "5.10.0")
            val fritz = version("fritz", "1.0-RC12")
            val torrent = version("bt", "1.10")
            val compose = version("compose", "1.6.0-alpha01")
            val koin = version("koin", "3.5.3")
            val koinKsp = version("koin-ksp", "1.3.0")
            val kotlinPoet = version("kotlinpoet", "1.16.0")
            val serialization = version("serialization", "1.6.2")

            library("logging", "ch.qos.logback", "logback-classic").version("1.4.1")

            plugin("ktor", "io.ktor.plugin").versionRef(ktor)
            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktor)
            library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef(ktor)
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
            library("ktor-server-tests", "io.ktor", "ktor-server-tests").versionRef(ktor)
            library("ktor-serialization", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktor)

            library("test-kotlin", "org.jetbrains.kotlin", "kotlin-test-junit").versionRef(kotlin)
            library("test-jupiter-api", "org.junit.jupiter", "junit-jupiter-api").versionRef(jupiter)
            library("test-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").versionRef(jupiter)

            library("koin-core", "io.insert-koin", "koin-core").versionRef(koin)
            library("koin-ktor", "io.insert-koin", "koin-ktor").versionRef(koin)
            library("koin-annotations", "io.insert-koin", "koin-annotations").versionRef(koinKsp)
            library("koin-ksp", "io.insert-koin", "koin-ksp-compiler").versionRef(koinKsp)

            plugin("ksp", "com.google.devtools.ksp").versionRef(ksp)
            library("kotlin-poet", "com.squareup", "kotlinpoet").versionRef(kotlinPoet)
            library("kotlin-ksp-api", "com.google.devtools.ksp", "symbol-processing-api").versionRef(ksp)

            plugin("serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef(kotlin)
            library("serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef(serialization)

            library("coroutines-test", "org.jetbrains.kotlinx", "kotlinx-coroutines-test").version("1.7.3")

            library("fritz-core", "dev.fritz2", "core").versionRef(fritz)
            library("fritz-annotation", "dev.fritz2", "lenses-annotation-processor").versionRef(fritz)

            library("torrent-core", "com.github.atomashpolskiy", "bt-core").versionRef(torrent)
            library("torrent-dht", "com.github.atomashpolskiy", "bt-dht").versionRef(torrent)

            plugin("compose", "org.jetbrains.compose").versionRef(compose)
            library("compose-runtime", "org.jetbrains.compose.runtime", "runtime").versionRef(compose)
            library("compose-foundation", "org.jetbrains.compose.foundation", "foundation").versionRef(compose)
            library("compose-material", "org.jetbrains.compose.material", "material").versionRef(compose)

            library("uuid", "com.benasher44", "uuid").version("0.8.2")
            library("datetime", "org.jetbrains.kotlinx", "kotlinx-datetime").version("0.5.0")
        }
    }
}
