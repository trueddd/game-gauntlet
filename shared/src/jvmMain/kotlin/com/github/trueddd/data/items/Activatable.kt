package com.github.trueddd.data.items

interface Activatable<out T : WheelItem> {

    val isActive: Boolean

    fun setActive(value: Boolean): T
}
