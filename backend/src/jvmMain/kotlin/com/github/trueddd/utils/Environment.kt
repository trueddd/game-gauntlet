package com.github.trueddd.utils

import java.util.Properties

object Environment {

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
