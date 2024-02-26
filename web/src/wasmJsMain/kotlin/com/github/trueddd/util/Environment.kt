package com.github.trueddd.util

fun serverAddress(): String = js("window.env.SERVER_ADDRESS")

fun isDevEnvironment(): Boolean = js("window.env.IS_DEV == \"1\"")
