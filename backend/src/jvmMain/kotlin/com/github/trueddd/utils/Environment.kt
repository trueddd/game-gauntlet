package com.github.trueddd.utils

import java.util.Properties

object Environment {

    private val appConfig by lazy { System.getenv().toProperties() }

    private const val DEFAULT_PORT = 8081

    fun resolveConfig(): Properties {
        return appConfig
    }

    val Port by lazy {
        System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT
    }

    val IsDev: Boolean by lazy {
        appConfig.getProperty("DEV") == "1"
    }

    val ClientAddress: String by lazy {
        appConfig.getProperty("CLIENT_ADDRESS")
    }

    val TwitchClientId: String by lazy {
        appConfig.getProperty("TWITCH_CLIENT_ID")
    }

    val DatabaseUrl: String by lazy {
        appConfig.getProperty("DB_URL")
    }
}
