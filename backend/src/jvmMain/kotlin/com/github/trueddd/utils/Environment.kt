package com.github.trueddd.utils

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

object Environment {

    private fun currentDir(): Path {
        return Paths.get(Environment::class.java.protectionDomain.codeSource.location.toURI())
    }

    private val appConfig by lazy { System.getenv().toProperties() }

    fun resolveConfig(): Properties {
        return appConfig
    }

    val Port by lazy {
        System.getenv("PORT")?.toIntOrNull() ?: 8081
    }

    val IsDev: Boolean by lazy {
        appConfig.getProperty("DEV") == "1"
    }

    val GamesDirectory: File by lazy {
        if (IsDev) {
            File(appConfig.getProperty("GAMES_DIR"))
        } else {
            currentDir().resolve("games").toFile()
        }
    }

    val GamesMagnetUri: String by lazy {
        appConfig.getProperty("GAMES_MAGNET_URI")
    }

    val ClientAddress: String by lazy {
        appConfig.getProperty("CLIENT_ADDRESS")
    }

    val DatabaseUrl: String by lazy {
        appConfig.getProperty("DB_URL")
    }

    val DatabaseUser: String by lazy {
        appConfig.getProperty("DB_USER")
    }

    val DatabasePassword: String by lazy {
        appConfig.getProperty("DB_PASS")
    }
}
