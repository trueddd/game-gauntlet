package com.github.trueddd.ui.rules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.github.trueddd.items.WheelItem

@Stable
sealed class WheelItemView(
    val name: String,
    val color: Color,
) {

    companion object {
        @Stable
        val All = listOf(Buff, Debuff, Event, PendingEvent, Inventory)
    }

    data object Buff : WheelItemView("Баффы", Color(WheelItem.Colors.BUFF))
    data object Debuff : WheelItemView("Дебаффы", Color(WheelItem.Colors.DEBUFF))
    data object Event : WheelItemView("События", Color(WheelItem.Colors.EVENT))
    data object PendingEvent : WheelItemView("Интерактивные события", Color(WheelItem.Colors.PENDING_EVENT))
    data object Inventory : WheelItemView("Предметы", Color(WheelItem.Colors.INVENTORY_ITEM))
}

@Composable
fun WheelItemChip(
    view: WheelItemView,
    selected: Boolean,
    onClick: () -> Unit = {},
) {
    val shape = RoundedCornerShape(8.dp)
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .background(if (selected) view.color.copy(alpha = 0.2f) else Color.Transparent, shape)
            .border(
                width = 1.dp,
                color = if (selected) view.color.copy(alpha = 0.8f) else view.color.copy(alpha = 0.6f),
                shape = shape
            )
            .pointerHoverIcon(PointerIcon.Hand)
            .clickable(onClick = onClick, indication = null, interactionSource = interactionSource)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(
            text = view.name,
            color = if (selected) view.color else view.color.copy(alpha = 0.6f),
        )
    }
}
