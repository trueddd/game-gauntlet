package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.without
import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.LoyalModerator}")
class LoyalModerator private constructor(override val uid: String) : WheelItem.InventoryItem(),
    Parametrized<Parameters.One<String>> {

    companion object {
        fun create() = LoyalModerator(uid = generateWheelItemUid())
    }

    override val id = Id(LoyalModerator)

    override val name = "Верный модер"

    override val description = """
        |Принимает выбранный стримером дебафф на себя и уничтожает его. 
        |Не имеет временных ограничений и может быть использовано в любой момент.
    """.removeTabs()

    override val parametersScheme: List<ParameterType>
        get() = listOf(ParameterType.MyItem(
            name = "Снимаемый дебафф",
            predicate = { it is Effect.Debuff && it !is EasterCakeBang }
        ))

    override fun getParameters(rawArguments: List<String>, currentState: GlobalState): Parameters.One<String> {
        return Parameters.One(rawArguments.getStringParameter())
    }

    override suspend fun use(usedBy: PlayerName, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            val targetDebuffId = getParameters(arguments, globalState).parameter1
            if (playerState.effects.firstOrNull { it.uid == targetDebuffId } is NonDroppable) {
                throw IllegalArgumentException("This debuff cannot be dispelled")
            }
            playerState.copy(
                effects = playerState.effects.without(targetDebuffId),
                inventory = playerState.inventory.without(uid),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(LoyalModerator)
        override fun create() = Companion.create()
    }
}
