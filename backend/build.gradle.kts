import io.gitlab.arturbosch.detekt.Detekt

plugins {
    kotlin("multiplatform")
    application
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
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
    }
    sourceSets {
        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                implementation(project(":annotations"))
                implementation(project(":common"))
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
                implementation(libs.ktor.server.caching)
                implementation(libs.ktor.server.auth.core)
                implementation(libs.ktor.server.auth.jwt)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization)
                implementation(libs.koin.core)
                implementation(libs.koin.annotations)
                implementation(libs.koin.ktor)
                implementation(libs.mongodb.driver)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project(":common"))
                implementation(libs.ktor.server.tests)
                implementation(libs.test.kotlin)
                implementation(libs.test.jupiter.engine)
                implementation(libs.test.jupiter.api)
                implementation(libs.coroutines.test)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    add("kspJvm", project(":di"))
    add("kspJvm", libs.koin.ksp)
}

tasks.named("build") {
    dependsOn(":common:build")
}

tasks.create("stage") {
    dependsOn("buildFatJar")
}

detekt {
    config.setFrom(file("detekt-config.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("build/reports/detekt.html"))
    }
}
