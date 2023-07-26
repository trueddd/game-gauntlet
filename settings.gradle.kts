rootProject.name = "game-gauntlet-backend"
include("di")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val kotlin = version("kotlin", "1.9.0")
            val ksp = version("ksp", "1.9.0-1.0.12")
            val ktor = version("ktor", "2.3.2")
            val jupiter = version("jupiter", "5.10.0")

            library("logging", "ch.qos.logback", "logback-classic").version("1.4.8")

            plugin("ktor", "io.ktor.plugin").versionRef(ktor)
            library("ktor-server-core", "io.ktor", "ktor-server-core-jvm").versionRef(ktor)
            library("ktor-server-netty", "io.ktor", "ktor-server-netty-jvm").versionRef(ktor)
            library("ktor-server-websockets", "io.ktor", "ktor-server-websockets-jvm").versionRef(ktor)
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation-jvm").versionRef(ktor)
            library("ktor-server-call-logging", "io.ktor", "ktor-server-call-logging-jvm").versionRef(ktor)
            library("ktor-server-headers-default", "io.ktor", "ktor-server-default-headers-jvm").versionRef(ktor)
            library("ktor-server-headers-conditional", "io.ktor", "ktor-server-conditional-headers-jvm").versionRef(ktor)
            library("ktor-server-cors", "io.ktor", "ktor-server-cors-jvm").versionRef(ktor)
            library("ktor-server-auth-core", "io.ktor", "ktor-server-auth-jvm").versionRef(ktor)
            library("ktor-server-auth-jwt", "io.ktor", "ktor-server-auth-jwt-jvm").versionRef(ktor)
            library("ktor-server-tests", "io.ktor", "ktor-server-tests-jvm").versionRef(ktor)
            library("ktor-serialization", "io.ktor", "ktor-serialization-kotlinx-json-jvm").versionRef(ktor)

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
        }
    }
}
