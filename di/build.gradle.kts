plugins {
    kotlin("jvm") version Versions.Kotlin
}

group = Config.PackageName
version = Config.Version

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:${Versions.KotlinKsp}")
    implementation("com.squareup:kotlinpoet:1.12.0")
    api(Dependency.Koin.Core)
    api(Dependency.Koin.Ktor)
    api(Dependency.Koin.Annotations)
}
