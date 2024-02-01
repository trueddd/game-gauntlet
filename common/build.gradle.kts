import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.ksp)
}

kotlin {
    jvm {
    }
    js(IR) {
        browser()
        binaries.library()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.library()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":annotations"))
                implementation(libs.serialization)
                implementation(libs.uuid)
                implementation(libs.datetime)
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            dependencies {
                api(project(":di"))
            }
        }
        val jsMain by getting {
        }
        val wasmJsMain by getting {
        }
    }
}

dependencies {
    add("kspJvm", project(":di"))
    add("kspJvm", libs.koin.ksp)
}
