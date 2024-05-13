#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")
package ${PACKAGE_NAME}
#end

import com.github.trueddd.utils.removeTabs
#if (${TYPE} == "event" || ${TYPE} == "item" || ${TYPE} == "pending")
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
#end
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class $NAME private constructor(override val uid: String)
#if (${TYPE} == "buff")
: WheelItem.Effect.Buff() {
#elseif (${TYPE} == "debuff")
: WheelItem.Effect.Debuff() {
#elseif (${TYPE} == "event")
: WheelItem.Event() {
#elseif (${TYPE} == "pending")
: WheelItem.PendingEvent() {
#else
: WheelItem.InventoryItem() {
#end

    companion object {
        fun create() = ${NAME}(uid = generateWheelItemUid())
    }

    override val id = Id(${NAME})

    override val name = TODO()

    override val description = """
        |TODO
    """.removeTabs()
    
#if (${TYPE} == "event")
    override suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState {
        return globalState
    }
    
#end
#if (${TYPE} == "item" || ${TYPE} == "pending")
    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return TODO()
    }
    
#end
    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(${NAME})
        override fun create() = Companion.create()
    }
}
