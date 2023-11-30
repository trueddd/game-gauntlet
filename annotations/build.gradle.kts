plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm {
    }
    js(IR) {
        browser()
        binaries.library()
    }
    sourceSets {
        val commonMain by getting
    }
}
