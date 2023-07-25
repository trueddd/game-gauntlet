package com.github.trueddd.core

import com.github.trueddd.data.Game
import org.koin.core.annotation.Single

// todo: provide game list from file
// todo: mock for tests
@Single
class GamesProvider {

    private val games = listOf(
        Game(id = Game.Id(0), name = "Game 1"),
        Game(id = Game.Id(1), name = "The Elder Game"),
        Game(id = Game.Id(2), name = "The Gamer: Wild Game"),
        Game(id = Game.Id(3), name = "Gamers Creed"),
        Game(id = Game.Id(4), name = "Just Gaming"),
    )

    fun roll(): Game {
        return games.random()
    }

    fun getById(id: Game.Id): Game? {
        return games.firstOrNull { it.id == id }
    }
}
