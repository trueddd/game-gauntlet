package com.github.trueddd.core.generator

import com.github.trueddd.core.events.Action
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator::class)
class GameDropGenerator : ActionGenerator<Action.GameDrop> {

    override val inputMatcher by lazy {
        Regex("drop ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(input: String): Action.GameDrop {
        val actor = inputMatcher.matchEntire(input)?.groupValues?.lastOrNull()!!
        val dice = rollDice()
        return Action.GameDrop(Participant(actor), dice)
    }
}
