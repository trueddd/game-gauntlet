import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.targets.js.binaryen.BinaryenExec
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.io.IOException

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.detekt)
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
                implementation(compose.components.resources)
                implementation(libs.compose.runtime)
                implementation(libs.compose.material)
                implementation(libs.compose.foundation)
                implementation(libs.koin.core)
                implementation("io.ktor:ktor-client-core:3.0.0-wasm2")
                implementation("io.ktor:ktor-client-js:3.0.0-wasm2")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0-wasm2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0-wasm2")
                implementation(libs.coil.core)
                implementation(libs.coil.compose)
                implementation(libs.coil.network)
            }
        }
    }
}

compose.resources {
    generateResClass = ResourcesExtension.ResourceClassGeneration.Always
}

tasks.named<BinaryenExec>("compileProductionExecutableKotlinWasmJsOptimize") {
    binaryenArgs = mutableListOf("-O1", "--all-features")
}

fun composePropertiesFromEnv(
    fileName: String,
    propertyKeys: List<String>,
) {
    val destinationDir = projectDir
        .resolve("src")
        .resolve("wasmJsMain")
        .resolve("resources")
    val file = destinationDir.resolve(fileName)
    if (!file.exists()) {
        file.createNewFile()
    } else {
        file.writeText("")
    }
    val properties = propertyKeys
        .associateWith { System.getenv(it) }
        .filterValues { it != null }
    val content = buildString {
        append("window.env = {")
        properties.forEach { (key, value) ->
            append("\"$key\":\"$value\",")
        }
        append("};")
    }
    val stream = file.outputStream()
    try {
        stream.write(content.toByteArray())
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        stream.close()
    }
}

tasks.create("mutateResources") {
    composePropertiesFromEnv(
        fileName = "env.js",
        propertyKeys = listOf(
            "SERVER_ADDRESS",
            "IS_DEV",
            "TWITCH_CLIENT_ID",
        )
    )
}

tasks.named("compileKotlinWasmJs") {
    dependsOn("mutateResources")
}

dependencies {
    detektPlugins("io.nlopez.compose.rules:detekt:0.4.4")
}

detekt {
    config.setFrom(file("detekt-config.yml"))
    buildUponDefaultConfig = false
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        html.outputLocation.set(file("web/build/reports/detekt.html"))
    }
}
