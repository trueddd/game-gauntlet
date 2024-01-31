package com.github.trueddd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.trueddd.data.GlobalState
import com.github.trueddd.items.WheelItem
import com.github.trueddd.theme.Colors
import com.github.trueddd.util.copyToClipBoard

@Stable
private val WheelItem.color: Color
    get() = when (this) {
        is WheelItem.PendingEvent -> Colors.WheelItem.PendingEvent
        is WheelItem.Event -> Colors.WheelItem.Event
        is WheelItem.InventoryItem -> Colors.WheelItem.InventoryItem
        is WheelItem.Effect.Debuff -> Colors.WheelItem.Debuff
        is WheelItem.Effect.Buff -> Colors.WheelItem.Buff
    }

@Composable
private fun RowScope.TableCell(content: @Composable BoxScope.() -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
    ) {
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WheelItems(items: List<WheelItem>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .background(item.color, RoundedCornerShape(4.dp))
                    .clickable { copyToClipBoard(item.uid) }
                    .pointerHoverIcon(PointerIcon.Hand)
                    .padding(2.dp)
            )
        }
    }
}

@Composable
fun RowScope.StateTableW(globalState: GlobalState) {
    Column(
        modifier = Modifier
            .weight(4f)
            .background(Colors.SecondaryBackground, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Row {
                TableCell {
                    Text(text = "Player")
                }
                TableCell {
                    Text(text = "Position")
                }
                TableCell {
                    Text(text = "Game")
                }
                TableCell {
                    Text(text = "Inventory")
                }
                TableCell {
                    Text(text = "Effects")
                }
                TableCell {
                    Text(text = "Pending events")
                }
            }
            globalState.players.forEach { (player, state) ->
                Row {
                    TableCell {
                        Text(text = player.displayName)
                    }
                    TableCell {
                        Text(text = "${state.position}")
                    }
                    TableCell {
                        Text(text = state.currentActiveGame?.game?.name ?: "-")
                    }
                    TableCell {
                        WheelItems(state.inventory)
                    }
                    TableCell {
                        WheelItems(state.effects)
                    }
                    TableCell {
                        WheelItems(state.pendingEvents)
                    }
                }
            }
        }
    }
}
