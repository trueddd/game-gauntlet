package com.github.trueddd.core.generator

import com.github.trueddd.core.ItemRoller
import com.github.trueddd.core.events.Action
import com.github.trueddd.data.Participant
import com.trueddd.github.annotations.IntoSet

@IntoSet(ActionGenerator::class)
class ItemReceiveGenerator(
    private val itemRoller: ItemRoller,
) : ActionGenerator<Action.ItemReceive> {

    override val inputMatcher by lazy {
        Regex("item ([a-z]+)", RegexOption.DOT_MATCHES_ALL)
    }

    override fun generate(input: String): Action.ItemReceive {
        val actor = inputMatcher.matchEntire(input)?.groupValues?.lastOrNull()!!
        val item = itemRoller.pick()
        return Action.ItemReceive(Participant(actor), item)
    }
}
