import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose)
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "wasmaggapp"
        browser {
            commonWebpackConfig {
                outputFileName = "aggwasm.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    port = 8081
                    static = (static ?: mutableListOf()).apply {
                        add(project.rootDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))
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
                api(libs.compose.runtime)
                api(libs.compose.material)
                api(libs.compose.foundation)
            }
        }
        val wasmJsMain by getting {
            dependencies {
                implementation(project(":common"))
                api(libs.compose.runtime)
                api(libs.compose.material)
                api(libs.compose.foundation)
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
