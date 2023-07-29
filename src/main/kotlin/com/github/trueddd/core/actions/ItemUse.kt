package com.github.trueddd.core.actions

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.utils.ActionGeneratorCreationException
import com.trueddd.github.annotations.IntoMap
import com.trueddd.github.annotations.IntoSet
import kotlinx.serialization.Serializable

@Serializable
data class ItemUse(
    val usedBy: Participant,
    val itemUid: String,
) : Action(Keys.ITEM_USE) {

    @IntoSet(Action.Generator.SET_TAG)
    class Generator : Action.Generator<ItemUse> {

        override val inputMatcher by lazy {
            Regex(
                pattern = "${Commands.ITEM_USE} ${Action.Generator.RegExpGroups.USER} ${Action.Generator.RegExpGroups.ITEM_UID}",
                option = RegexOption.DOT_MATCHES_ALL
            )
        }

        override fun generate(matchResult: MatchResult): ItemUse {
            val user = matchResult.groupValues.getOrNull(1)
                ?: throw ActionGeneratorCreationException("Couldn't parse participant from input `${matchResult.value}`")
            val itemUid = matchResult.groupValues.getOrNull(2)
                ?: throw ActionGeneratorCreationException("Couldn't parse itemUid from input `${matchResult.value}`")
            return ItemUse(Participant(user), itemUid)
        }
    }

    @IntoMap(mapName = Action.Handler.MAP_TAG, key = Keys.ITEM_USE)
    class Handler : Action.Handler<ItemUse> {

        override suspend fun handle(action: ItemUse, currentState: GlobalState): GlobalState {
            val item = currentState.players[action.usedBy]?.inventory
                ?.firstOrNull { it.uid == action.itemUid }
                ?: return currentState
            return item.use(action.usedBy, currentState)
        }
    }
}
