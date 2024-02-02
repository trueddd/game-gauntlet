package com.github.trueddd.utils

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

object Environment {

    private fun currentDir(): Path {
        return Paths.get(Environment::class.java.protectionDomain.codeSource.location.toURI())
    }

    internal fun resolveConfig(name: String): Properties {
        val propertiesFile = currentDir().toFile().parentFile.resolve(name)
        return if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { Properties().apply { load(it) } }
        } else {
            System.getenv().toProperties()
        }
    }

    private val appConfig by lazy { resolveConfig("app.properties") }

    val IsDev: Boolean by lazy {
        appConfig.getProperty("DEV") == "1"
    }

    val GamesDirectory: File by lazy {
        File(appConfig.getProperty("GAMES_DIR"))
    }

    val GamesMagnetUri: String by lazy {
        appConfig.getProperty("GAMES_MAGNET_URI")
    }

    val ClientAddress: String by lazy {
        appConfig.getProperty("CLIENT_ADDRESS")
    }
}
