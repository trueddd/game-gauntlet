import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs = listOf("-Xcontext-receivers", "-Xexpect-actual-classes")
    }
    jvm()
    jvmToolchain(18)
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":annotations"))
                implementation(project(":map"))
                implementation(libs.serialization)
                implementation(libs.uuid)
                api(libs.datetime)
                api(libs.kotlin.immutable)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.test.kotlin)
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                api(project(":di"))
            }
        }
        val wasmJsMain by getting {
            kotlin.srcDir("build/generated/ksp/wasmJs/wasmJsMain/kotlin")
        }
    }
}

dependencies {
    add("kspJvm", project(":di"))
    add("kspJvm", libs.koin.ksp)
    add("kspWasmJs", project(":di"))
}
