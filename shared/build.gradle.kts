import java.io.IOException
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        browser {
            commonWebpackConfig(Action<KotlinWebpackConfig> {
                outputFileName = "agg.js"
            })
        }
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
                implementation(libs.ktor.server.headers.default)
                implementation(libs.ktor.server.headers.conditional)
                implementation(libs.ktor.server.auth.core)
                implementation(libs.ktor.server.auth.jwt)
                implementation(libs.ktor.serialization)
                implementation(libs.koin.ktor)
            }
        }
        jvmMain.dependsOn(commonMain)
        val jsMain by getting {
            dependencies {
            }
        }
        jsMain.dependsOn(commonMain)
        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.server.tests)
                implementation(libs.test.kotlin)
                implementation(libs.test.jupiter.engine)
                implementation(libs.test.jupiter.api)
                implementation(libs.coroutines.test)
            }
        }
        jvmTest.dependsOn(commonTest)
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
