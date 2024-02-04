package com.github.trueddd.util

fun serverAddress(): String = js("window.env.SERVER_ADDRESS")
