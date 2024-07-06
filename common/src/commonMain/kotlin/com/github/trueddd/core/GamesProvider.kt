package com.github.trueddd.core

import com.github.trueddd.data.Game
import com.github.trueddd.map.Genre

interface GamesProvider {

    fun roll(genre: Genre? = null): Game

    fun getById(id: Game.Id): Game?

    fun listAll(): List<Game>
}
