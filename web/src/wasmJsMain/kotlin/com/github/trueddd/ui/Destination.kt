package com.github.trueddd.ui

import androidx.compose.runtime.Stable

@Stable
sealed class Destination(val name: String) {

    companion object {
        fun all() = listOf(Rules, Map, Dashboard, Wheels, Games, Profile)
    }

    open val isPrivate = false

    data object Rules : Destination("Правила")
    data object Map : Destination("Карта")
    data object Dashboard : Destination("Панель управления") {
        override val isPrivate = true
    }
    data object Games : Destination("Загрузка игр") {
        override val isPrivate = true
    }
    data object Profile : Destination("Профиль")
    data object Wheels : Destination("Колеса")
}
