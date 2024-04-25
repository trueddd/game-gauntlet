plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":annotations"))
                implementation(libs.kotlin.ksp.api)
                implementation(libs.kotlin.poet)
            }
        }
    }
}
