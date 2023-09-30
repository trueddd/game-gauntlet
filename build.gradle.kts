plugins {
    kotlin("multiplatform").apply(false)
    alias(libs.plugins.ktor).apply(false)
    alias(libs.plugins.serialization).apply(false)
    alias(libs.plugins.ksp).apply(false)
}
