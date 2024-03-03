package com.github.trueddd.core

import com.github.trueddd.data.Game

// todo: provide game list from file
interface GamesProvider {

    fun roll(): Game

    fun getById(id: Game.Id): Game?
}
