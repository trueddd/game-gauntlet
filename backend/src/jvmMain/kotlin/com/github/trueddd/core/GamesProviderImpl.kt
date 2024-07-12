package com.github.trueddd.core

import com.github.trueddd.data.Game
import com.github.trueddd.map.Genre
import com.github.trueddd.utils.Log
import com.github.trueddd.utils.getResourceFile
import com.github.trueddd.utils.serialization
import kotlinx.serialization.SerializationException
import org.koin.core.annotation.Single

@Single
class GamesProviderImpl : GamesProvider {

    private companion object {
        const val TAG = "GamesProvider"
    }

    private fun decodeGenreOrNull(input: String): Genre? {
        return try {
            serialization.decodeFromString<Genre>("\"$input\"")
        } catch (e: SerializationException) {
            Log.error(TAG, "Failed to decode genre ($input): ${e.message}")
            null
        } catch (e: IllegalArgumentException) {
            Log.error(TAG, "Decoded value is not valid ($input): ${e.message}")
            null
        }
    }

    private val games = run {
        val fileContent = getResourceFile("games")
            ?.bufferedReader()
            ?.readLines()
            ?: error("No `games` file was found")
        var parsedGamesCount = 0
        var corruptedGamesCount = 0
        val games = fileContent.mapNotNull { content ->
            val (name, rawGenre) = content.split("|")
            val genre = decodeGenreOrNull(rawGenre) ?: run {
                corruptedGamesCount++
                return@mapNotNull null
            }
            Game(Game.Id(parsedGamesCount++), name, genre)
        }
        Log.info(TAG, "Games parsing result: ($parsedGamesCount/$corruptedGamesCount)")
        return@run games
    }

    override fun roll(genre: Genre?): Game {
        val gamesList = when (genre) {
            null -> games
            else -> games.filter { it.genre == genre }
        }
        return gamesList.random()
    }

    override fun getById(id: Game.Id): Game? {
        return games.firstOrNull { it.id == id }
    }

    override fun listAll(): List<Game> {
        return games
    }
}
