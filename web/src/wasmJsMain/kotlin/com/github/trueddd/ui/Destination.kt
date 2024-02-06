package com.github.trueddd.ui

sealed class Destination(val name: String) {

    companion object {
        fun all() = listOf(Rules, Map, Dashboard, Games)
    }

    data object Rules : Destination("Правила")

    data object Map : Destination("Карта")

    data object Dashboard : Destination("Панель управления")

    data object Games : Destination("Загрузка игр")
}
