package com.github.trueddd.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import com.github.trueddd.items.WheelItem
import com.github.trueddd.theme.Colors

@Stable
val WheelItem.color: Color
    get() = when (this) {
        is WheelItem.PendingEvent -> Colors.WheelItem.PendingEvent
        is WheelItem.Event -> Colors.WheelItem.Event
        is WheelItem.InventoryItem -> Colors.WheelItem.InventoryItem
        is WheelItem.Effect.Debuff -> Colors.WheelItem.Debuff
        is WheelItem.Effect.Buff -> Colors.WheelItem.Buff
    }
