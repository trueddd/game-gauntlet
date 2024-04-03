package com.github.trueddd.util

import androidx.compose.runtime.Stable
import com.github.trueddd.data.Game
import com.github.trueddd.items.WheelItem

@Stable
val WheelItem.typeLocalized: String
    get() = when (this) {
        is WheelItem.InventoryItem -> "Предмет"
        is WheelItem.Effect.Buff -> "Бафф"
        is WheelItem.Effect.Debuff -> "Дебафф"
        is WheelItem.Event -> "Событие"
        is WheelItem.PendingEvent -> "Отложенное событие"
    }

@Stable
val Game.Genre.localized: String
    get() = when (this) {
        Game.Genre.Runner -> "Бегалки"
        Game.Genre.Business -> "Бизнес"
        Game.Genre.Puzzle -> "Головоломки"
        Game.Genre.PointAndClick -> "Поиск предметов"
        Game.Genre.Shooter -> "Стрелялки"
        Game.Genre.ThreeInRow -> "Три в ряд"
        Game.Genre.Special -> "Специальный сектор"
    }

@Stable
val Game.Status.localized: String
    get() = when (this) {
        Game.Status.InProgress -> "В процессе"
        Game.Status.Finished -> "Пройдено"
        Game.Status.Dropped -> "Дропнуто"
        Game.Status.Rerolled -> "Реролл"
        Game.Status.Next -> "Следующая"
    }
