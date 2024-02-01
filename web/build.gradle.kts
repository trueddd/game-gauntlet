import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "agg"
        browser {
            commonWebpackConfig {
                outputFileName = "agg.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    port = 8080
                    static = (static ?: mutableListOf()).apply {
                        add(project.rootDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":common"))
                api(libs.compose.runtime)
                api(libs.compose.material)
                api(libs.compose.foundation)
                implementation(libs.koin.core)
                implementation("io.ktor:ktor-client-core:3.0.0-wasm2")
                implementation("io.ktor:ktor-client-js:3.0.0-wasm2")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm2")
            }
        }
    }
}

compose.experimental {
    web.application {}
}
