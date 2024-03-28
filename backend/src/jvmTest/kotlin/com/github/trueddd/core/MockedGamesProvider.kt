package com.github.trueddd.core

import com.github.trueddd.data.Game

internal class MockedGamesProvider : GamesProvider {

    private val games = listOf(
        Game(id = Game.Id(0), name = "Game 1", Game.Genre.Puzzle),
        Game(id = Game.Id(1), name = "The Elder Game", Game.Genre.Runner),
        Game(id = Game.Id(2), name = "The Gamer: Wild Game", Game.Genre.Shooter),
        Game(id = Game.Id(3), name = "Gamers Creed", Game.Genre.Runner),
        Game(id = Game.Id(4), name = "Just Gaming", Game.Genre.PointAndClick),
        Game(id = Game.Id(5), name = "Супер Корова", Game.Genre.Runner),
    )

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
