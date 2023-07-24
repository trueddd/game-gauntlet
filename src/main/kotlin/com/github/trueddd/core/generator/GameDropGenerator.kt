package com.github.trueddd.core.generator

import com.github.trueddd.core.actions.GameDrop
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator.TAG)
class GameDropGenerator : ActionGenerator<GameDrop> {

    override val inputMatcher by lazy {
        Regex("drop ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(matchResult: MatchResult): GameDrop {
        val actor = matchResult.groupValues.lastOrNull()!!
        val dice = rollDice()
        return GameDrop(Participant(actor), dice)
    }
}
