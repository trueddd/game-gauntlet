package com.github.trueddd.utils

import java.io.File

object Environment {

    val isDev: Boolean by lazy {
        System.getenv("DEV") == "1"
    }

    val gamesDirectory: File by lazy {
        File(System.getenv("GAMES_DIR"))
    }

    val gamesMagnetUri: String by lazy {
        System.getenv("GAMES_MAGNET_URI")
    }
}
