package com.github.trueddd.ui.wheels

import com.github.trueddd.data.Rollable

class CoinValue(val value: Boolean) : Rollable {
    companion object {
        val All = listOf(CoinValue(true), CoinValue(false))
    }
    override val name: String
        get() = if (value) "Heads" else "Tails"
    override val description: String
        get() = throw IllegalStateException()
    override val color: Long
        get() = throw IllegalStateException()
}
