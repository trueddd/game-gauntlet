package com.github.trueddd.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
sealed class Destination(
    val name: String,
    val icon: ImageVector,
    val disabledIcon: ImageVector,
) {

    companion object {
        fun all() = listOf(Rules, Map, Dashboard, Wheels, Games, Profile)
    }

    open val isPrivate = false

    data object Rules : Destination("Правила", Icons.AutoMirrored.Rounded.List, Icons.AutoMirrored.Outlined.List)
    data object Map : Destination("Карта", Icons.Rounded.LocationOn, Icons.Outlined.LocationOn)
    data object Dashboard : Destination("Панель управления", Icons.Rounded.Menu, Icons.Outlined.Menu) {
        override val isPrivate = true
    }

    data object Games : Destination("Загрузка игр", Icons.Rounded.Search, Icons.Outlined.Search) {
        override val isPrivate = true
    }

    data object Profile : Destination("Профиль", Icons.Rounded.AccountCircle, Icons.Outlined.AccountCircle)
    data object Wheels : Destination("Колеса", Icons.Rounded.Refresh, Icons.Outlined.Refresh)
}
