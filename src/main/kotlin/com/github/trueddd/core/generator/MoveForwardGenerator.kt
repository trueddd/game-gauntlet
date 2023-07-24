package com.github.trueddd.core.generator

import com.github.trueddd.core.actions.BoardMove
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.rollDice
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator.TAG)
class MoveForwardGenerator : ActionGenerator<BoardMove> {

    override val inputMatcher by lazy {
        Regex("roll ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(matchResult: MatchResult): BoardMove {
        val actor = matchResult.groupValues.lastOrNull()!!
        val dice = rollDice()
        return BoardMove(Participant(actor), dice)
    }
}
