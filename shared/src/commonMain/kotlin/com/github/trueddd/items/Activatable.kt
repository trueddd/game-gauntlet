package com.github.trueddd.items

interface Activatable<out T : WheelItem> {

    val isActive: Boolean

    fun setActive(value: Boolean): T
}
