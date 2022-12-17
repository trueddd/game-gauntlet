package com.github.trueddd.core.generator

import com.github.trueddd.core.ItemRoller
import com.github.trueddd.core.events.ItemReceive
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator.TAG)
class ItemReceiveGenerator(
    private val itemRoller: ItemRoller,
) : ActionGenerator<ItemReceive> {

    override val inputMatcher by lazy {
        Regex("item ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(matchResult: MatchResult): ItemReceive {
        val actor = matchResult.groupValues.lastOrNull()!!
        val item = itemRoller.pick()
        return ItemReceive(Participant(actor), item)
    }
}
