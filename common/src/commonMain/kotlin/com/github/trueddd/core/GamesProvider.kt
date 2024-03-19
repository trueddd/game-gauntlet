package com.github.trueddd.core

import com.github.trueddd.data.Game

interface GamesProvider {

    fun roll(genre: Game.Genre? = null): Game

    fun getById(id: Game.Id): Game?

    fun listAll(): List<Game>
}
