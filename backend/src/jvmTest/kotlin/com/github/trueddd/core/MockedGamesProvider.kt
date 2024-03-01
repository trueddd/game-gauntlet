package com.github.trueddd.core

import com.github.trueddd.data.Game

internal class MockedGamesProvider : GamesProvider {

    private val games = listOf(
        Game(id = Game.Id(0), name = "Game 1"),
        Game(id = Game.Id(1), name = "The Elder Game"),
        Game(id = Game.Id(2), name = "The Gamer: Wild Game"),
        Game(id = Game.Id(3), name = "Gamers Creed"),
        Game(id = Game.Id(4), name = "Just Gaming"),
        Game(id = Game.Id(5), name = "Супер Корова"),
    )

    override fun roll(): Game {
        return games.random()
    }

    override fun getById(id: Game.Id): Game? {
        return games.firstOrNull { it.id == id }
    }
}
