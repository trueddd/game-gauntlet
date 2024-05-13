package com.github.trueddd.util

fun serverAddress(): String = js("window.env.SERVER_ADDRESS")

fun isDevEnvironment(): Boolean = js("window.env.IS_DEV == \"1\"")

fun twitchClientId(): String = js("window.env.TWITCH_CLIENT_ID")

fun authRedirectUri(): String {
    return if (isDevEnvironment()) {
        "http://localhost:8080"
    } else {
        "https://trueddd.github.io/game-gauntlet"
    }
}

const val gamesDownloadLink = "https://webtor.io/6a13496f74f33d4f71ef6e402482d81ee55d3c8f"
