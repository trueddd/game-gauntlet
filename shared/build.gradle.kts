import java.io.IOException

plugins {
    kotlin("multiplatform")
    application
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
    jvm {
        withJava()
        compilations.all {
            kotlinOptions.jvmTarget = "11"
            kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
        }
    }
    js(IR) {
        browser()
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.serialization)
                implementation(project(":annotations"))
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                implementation(project(":di"))
                implementation(libs.logging)
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.websockets)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.server.call.logging)
                implementation(libs.ktor.server.cors)
                implementation(libs.ktor.server.partial)
                implementation(libs.ktor.server.autohead)
                implementation(libs.ktor.server.headers.default)
                implementation(libs.ktor.server.headers.conditional)
                implementation(libs.ktor.server.auth.core)
                implementation(libs.ktor.server.auth.jwt)
                implementation(libs.ktor.serialization)
                implementation(libs.koin.ktor)
                implementation(libs.torrent.core)
                implementation(libs.torrent.dht)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(libs.fritz.core)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization)
                implementation(npm("tailwindcss", "3.3.5"))
                implementation(devNpm("postcss", "8.4.31"))
                implementation(devNpm("postcss-loader", "7.3.3"))
                implementation(devNpm("autoprefixer", "10.4.16"))
                implementation(devNpm("css-loader", "6.8.1"))
                implementation(devNpm("style-loader", "3.3.3"))
                implementation(devNpm("cssnano", "6.0.1"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.server.tests)
                implementation(libs.test.kotlin)
                implementation(libs.test.jupiter.engine)
                implementation(libs.test.jupiter.api)
                implementation(libs.coroutines.test)
            }
        }
        val jsTest by getting {
        }
    }
}

dependencies {
    add("kspJvm", project(":di"))
    add("kspJvm", libs.koin.ksp)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named("buildFatJar") {
    doLast {
        val destinationDir = buildDir.resolve("libs")
        val jwtFile = destinationDir.resolve("jwt.properties")
        if (!jwtFile.exists()) {
            jwtFile.createNewFile()
        } else {
            jwtFile.writeText("")
        }
        val properties = listOf(
            "JWT_AUDIENCE",
            "JWT_DOMAIN",
            "JWT_REALM",
            "JWT_SECRET",
        ).associateWith { System.getenv(it) }
            .filterValues { it != null }
            .toProperties()
        println(properties.toString())
        val stream = jwtFile.outputStream()
        try {
            properties.store(stream, null)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            stream.close()
        }
    }
}
