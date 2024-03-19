package com.github.trueddd.core

import com.github.trueddd.data.Game
import com.github.trueddd.utils.Log
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.nio.file.Paths

@Single
class GamesProviderImpl : GamesProvider {

    private companion object {
        const val TAG = "GamesProvider"
    }

    private fun decodeGenreOrNull(input: String): Game.Genre? {
        return try {
            Json.decodeFromString<Game.Genre>(input)
        } catch (e: Exception) {
            null
        }
    }

    private val games = run {
        val fileContent = Paths.get("src/jvmMain/resources/games").toFile().readText()
        Log.info(TAG, "games file length ${fileContent.length}")
        var parsedGamesCount = 0
        var corruptedGamesCount = 0
        val games = fileContent.lines()
            .mapNotNull { content ->
                val (name, rawGenre) = content.split("|")
                    .takeIf { it.isNotEmpty() }
                    ?: run {
                        corruptedGamesCount++
                        return@mapNotNull null
                    }
                val genre = decodeGenreOrNull(rawGenre) ?: run {
                    corruptedGamesCount++
                    return@mapNotNull null
                }
                Game(Game.Id(parsedGamesCount++), name, genre)
            }
        Log.info(TAG, "Games parsing result: ($parsedGamesCount/$corruptedGamesCount)")
        return@run games
    }

    override fun roll(genre: Game.Genre?): Game {
        return games.random()
    }

    override fun getById(id: Game.Id): Game? {
        return games.firstOrNull { it.id == id }
    }

    override fun listAll(): List<Game> {
        return games
    }
}
