package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class UnrealBoost private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = UnrealBoost(uid = generateWheelItemUid())
    }

    override val id = Id.UnrealBoost

    override val name = "Нереальный буст"

    override val description = """
        Если участник, наролливший этот пункт сможет простримить 24 часа с учетом текущего времени трансляции, 
        то он получает +3 к следующим 5 броскам кубика. 
        Если стример испугался, то он может спокойно реролльнуть колесо без штрафа.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val isSuccessful = arguments.getBooleanParameter()
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = if (isSuccessful) playerState.effects + Buff.create() else playerState.effects,
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.UnrealBoost
        override fun create() = Companion.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int,
        override val chargesLeft: Int
    ) : Effect.Buff(), DiceRollModifier, WithCharges<Buff> {
        companion object {
            fun create() = Buff(uid = generateWheelItemUid(), modifier = 3, chargesLeft = 5)
        }
        override val id = Id.UnrealBoost
        override val name = "Нереальный буст"
        override val description = """
            Ты легенда. +${modifier.absoluteValue} к броску кубика на ход.
        """.trimIndent()
        override val maxCharges = 5

        override fun useCharge(): WithCharges<UnrealBoost.Buff> {
            return Buff(uid, modifier, chargesLeft - 1)
        }
    }
}
