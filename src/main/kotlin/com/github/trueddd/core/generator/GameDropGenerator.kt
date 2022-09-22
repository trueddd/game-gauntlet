package com.github.trueddd.core.generator

import com.github.trueddd.core.Action
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.rollDice

class GameDropGenerator : ActionGenerator<Action.DiceRoll.GameDrop> {

    override val inputMatcher by lazy {
        Regex("drop ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(input: String): Action.DiceRoll.GameDrop {
        val actor = inputMatcher.matchEntire(input)?.groupValues?.lastOrNull()!!
        val dice = rollDice()
        return Action.DiceRoll.GameDrop(Participant(actor), dice)
    }
}
