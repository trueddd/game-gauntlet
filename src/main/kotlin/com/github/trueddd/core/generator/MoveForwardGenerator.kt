package com.github.trueddd.core.generator

import com.github.trueddd.core.StateHolder
import com.github.trueddd.core.events.BoardMove
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator.TAG)
class MoveForwardGenerator(
    private val stateHolder: StateHolder,
) : ActionGenerator<BoardMove> {

    override val inputMatcher by lazy {
        Regex("roll ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(input: String): BoardMove {
        val actor = inputMatcher.matchEntire(input)?.groupValues?.lastOrNull()!!
        val modifier = stateHolder.globalStateFlow.value.players[Participant(actor)]?.diceModifier ?: 0
        val dice = rollDice()
        return BoardMove(Participant(actor), dice, modifier)
    }
}
