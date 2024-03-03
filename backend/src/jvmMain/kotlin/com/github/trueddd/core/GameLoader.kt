package com.github.trueddd.core

import java.io.File

interface GameLoader {
    suspend fun loadGame(gameName: String): File?
}
