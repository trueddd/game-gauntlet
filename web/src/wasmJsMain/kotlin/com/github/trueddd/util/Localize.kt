package com.github.trueddd.util

import androidx.compose.runtime.Stable
import com.github.trueddd.data.Game
import com.github.trueddd.items.WheelItem
import com.github.trueddd.ui.res.StringResources

@Stable
val WheelItem.typeLocalized: String
    get() = when (this) {
        is WheelItem.InventoryItem -> StringResources.WheelItem.InventoryItem
        is WheelItem.Effect.Buff -> StringResources.WheelItem.Buff
        is WheelItem.Effect.Debuff -> StringResources.WheelItem.Debuff
        is WheelItem.Event -> StringResources.WheelItem.Event
        is WheelItem.PendingEvent -> StringResources.WheelItem.PendingEvent
    }

@Stable
val Game.Genre.localized: String
    get() = when (this) {
        Game.Genre.Runner -> StringResources.GameGenre.Runner
        Game.Genre.Business -> StringResources.GameGenre.Business
        Game.Genre.Puzzle -> StringResources.GameGenre.Puzzle
        Game.Genre.PointAndClick -> StringResources.GameGenre.PointAndClick
        Game.Genre.Shooter -> StringResources.GameGenre.Shooter
        Game.Genre.ThreeInRow -> StringResources.GameGenre.ThreeInRow
        Game.Genre.Special -> StringResources.GameGenre.Special
    }

@Stable
val Game.Status.localized: String
    get() = when (this) {
        Game.Status.InProgress -> StringResources.GameStatus.InProgress
        Game.Status.Finished -> StringResources.GameStatus.Finished
        Game.Status.Dropped -> StringResources.GameStatus.Dropped
        Game.Status.Rerolled -> StringResources.GameStatus.Rerolled
        Game.Status.Next -> StringResources.GameStatus.Next
    }
