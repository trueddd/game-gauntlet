plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.ksp.api)
    implementation(libs.kotlin.poet)
    api(libs.koin.core)
    api(libs.koin.annotations)
}
