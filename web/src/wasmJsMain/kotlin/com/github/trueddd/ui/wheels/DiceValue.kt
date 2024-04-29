package com.github.trueddd.ui.wheels

import com.github.trueddd.data.Rollable

class DiceValue(val value: Int) : Rollable {
    companion object {
        val All = (1..6).map { DiceValue(it) }
    }
    override val name: String
        get() = value.toString()
    override val description: String
        get() = throw IllegalStateException()
    override val color: Long
        get() = throw IllegalStateException()
}
