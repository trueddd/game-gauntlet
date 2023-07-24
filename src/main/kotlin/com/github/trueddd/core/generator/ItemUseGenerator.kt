package com.github.trueddd.core.generator

import com.github.trueddd.core.actions.ItemUse
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator.TAG)
class ItemUseGenerator : ActionGenerator<ItemUse> {

    override val inputMatcher by lazy {
        Regex("use ([a-z]+) ([0-9]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(matchResult: MatchResult): ItemUse {
        val user = matchResult.groupValues[1]
        val itemUid = matchResult.groupValues[2].toLong()
        return ItemUse(Participant(user), itemUid)
    }
}
