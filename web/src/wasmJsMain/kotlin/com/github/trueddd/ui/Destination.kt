package com.github.trueddd.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.trueddd.ui.res.icons.map.Map
import com.github.trueddd.ui.res.icons.rules.Rules

@Stable
sealed class Destination(
    val name: String,
    val icon: ImageVector,
    val disabledIcon: ImageVector,
    val requireAuth: Boolean = false,
) {

    companion object {
        fun all() = listOf(Rules, Items, Map, Dashboard, Wheels, Profile)
    }

    data object Rules : Destination(
        "Правила",
        Icons.Rounded.Rules,
        Icons.Outlined.Rules
    )

    data object Items : Destination(
        "Предметы",
        Icons.Rounded.Rules,
        Icons.Outlined.Rules
    )

    data object Map : Destination(
        "Карта",
        Icons.Rounded.Map,
        Icons.Outlined.Map
    )

    data object Dashboard : Destination(
        "Панель управления",
        Icons.Rounded.Menu,
        Icons.Outlined.Menu,
        requireAuth = true
    )

    data object Profile : Destination(
        "Профиль",
        Icons.Rounded.AccountCircle,
        Icons.Outlined.AccountCircle
    )

    data object Wheels : Destination(
        "Колеса",
        Icons.Rounded.Refresh,
        Icons.Outlined.Refresh,
        requireAuth = true
    )
}
