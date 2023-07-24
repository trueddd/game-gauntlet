package com.github.trueddd.core.handler

import com.github.trueddd.core.actions.Action
import com.github.trueddd.core.actions.GameDrop
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.items.DropReverse
import com.github.trueddd.utils.coerceDiceValue
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.GameDrop)
class GameDropHandler : ActionConsumer<GameDrop> {

    override suspend fun consume(action: GameDrop, currentState: GlobalState): GlobalState {
        return currentState.updatePlayer(action.rolledBy) { playerState ->
            val moveValue = if (playerState.effects.any { it is DropReverse }) {
                coerceDiceValue(action.diceValue + playerState.diceModifier)
            } else {
                -coerceDiceValue(action.diceValue)
            }
            val finalPosition = (playerState.position + moveValue).coerceAtLeast(0)
            playerState.copy(
                position = finalPosition,
                effects = playerState.effects.filter { it !is DropReverse },
            )
        }
    }
}
