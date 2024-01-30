package com.github.trueddd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.trueddd.data.GlobalState

@Composable
private fun RowScope.TableCell(content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .weight(1f)
    ) {
        content()
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
                        Text(text = state.inventory.joinToString { "${it.name}|${it.uid}" })
                    }
                    TableCell {
                        Text(text = state.effects.joinToString { "${it.name}|${it.uid}" })
                    }
                    TableCell {
                        Text(text = state.pendingEvents.joinToString { "${it.name}|${it.uid}" })
                    }
                }
            }
        }
    }
}
