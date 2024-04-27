package com.github.trueddd.ui.wheels

import com.github.trueddd.data.Rollable

class DiceValue(val value: Int) : Rollable {
    override val name: String
        get() = value.toString()
    override val description: String
        get() = throw IllegalStateException()
    override val color: Long
        get() = throw IllegalStateException()
}
