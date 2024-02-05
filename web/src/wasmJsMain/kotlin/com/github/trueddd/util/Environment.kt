package com.github.trueddd.util

fun serverAddress(): String = js("window.env.SERVER_ADDRESS")

val wsProtocol: String
    get() = if (isDevEnvironment()) "ws" else "wss"

val httpProtocol: String
    get() = if (isDevEnvironment()) "http" else "https"

fun isDevEnvironment(): Boolean = js("window.env.IS_DEV == \"1\"")
