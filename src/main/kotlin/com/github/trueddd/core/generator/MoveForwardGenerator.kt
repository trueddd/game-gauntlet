package com.github.trueddd.core.generator

import com.github.trueddd.core.Action
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.rollDice
import kotlinx.coroutines.flow.StateFlow

class MoveForwardGenerator(
    private val globalState: StateFlow<GlobalState>,
) : ActionGenerator<Action.DiceRoll.BoardMoveAhead> {

    override val inputMatcher by lazy {
        Regex("roll ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(input: String): Action.DiceRoll.BoardMoveAhead {
        val actor = inputMatcher.matchEntire(input)?.groupValues?.lastOrNull()!!
        val modifier = globalState.value.players[Participant(actor)]?.diceModifier ?: 0
        val dice = rollDice()
        return Action.DiceRoll.BoardMoveAhead(Participant(actor), dice, modifier)
    }
}
