package com.github.trueddd.core.handler

import com.github.trueddd.core.events.Action
import com.github.trueddd.core.events.GameDrop
import com.github.trueddd.data.GlobalState
import com.github.trueddd.utils.coerceDiceValue
import com.trueddd.github.annotations.IntoMap

@IntoMap(mapName = ActionConsumer.TAG, key = Action.Keys.GameDrop)
class GameDropHandler : ActionConsumer<GameDrop> {

    override suspend fun consume(action: GameDrop, currentState: GlobalState): GlobalState {
        val newPlayersState = currentState.players
            .mapValues { (participant, playerState) ->
                if (action.rolledBy == participant) {
                    val moveValue = if (playerState.dropPenaltyReversed) {
                        coerceDiceValue(action.diceValue + playerState.diceModifier)
                    } else {
                        -coerceDiceValue(action.diceValue)
                    }
                    playerState.copy(
                        position = playerState.position + moveValue,
                        dropPenaltyReversed = false,
                    )
                } else {
                    playerState
                }
            }
        return currentState.copy(players = newPlayersState)
    }
}
