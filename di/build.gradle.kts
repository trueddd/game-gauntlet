plugins {
    kotlin("jvm") version Versions.Kotlin
}

group = Config.PackageName
version = Config.Version

repositories {
    mavenCentral()
}

dependencies {
    implementation(Dependency.SymbolProcessingApi)
    implementation(Dependency.KotlinPoet)
    api(Dependency.Koin.Core)
    api(Dependency.Koin.Ktor)
    api(Dependency.Koin.Annotations)
}
