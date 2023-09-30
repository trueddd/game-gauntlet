package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue

@Serializable
class MongoliaDoesNotExist private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = MongoliaDoesNotExist(uid = generateWheelItemUid())
    }

    override val id = Id.MongoliaDoesNotExist

    override val name = "Монголии не существует"

    override val description = """
        Просматривается данное видео (https://youtu.be/fPjepiWWPkY). 
        Стример интересуется, есть ли в чате хоть один монгол, и он (предполагаемый монгол) может это доказать, 
        за время пока идет видеоролик, то стример получает +2 к следующему броску кубика. 
        Если в чате нет монголов или никто из претендентов не смог это доказать, то -2 к следующему броску кубика. 
        Если стримеру уже было доказано, что Монголия существует, то он рероллит этот пункт.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        val isSuccessful = arguments.getBooleanParameter()
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + if (isSuccessful) Buff.create() else Debuff.create(),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.MongoliaDoesNotExist
        override fun create() = MongoliaDoesNotExist.create()
    }

    @Serializable
    class Buff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Buff(), DiceRollModifier {
        companion object {
            fun create() = Buff(uid = generateWheelItemUid(), modifier = 2)
        }
        override val id = Id.MongoliaDoesNotExist
        override val name = "Монголия существует"
        override val description = """
            Монголия существует. И теперь все это знают. +${modifier.absoluteValue} к броску кубика на ход.
        """.trimIndent()
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int
    ) : Effect.Debuff(), DiceRollModifier {
        companion object {
            fun create() = Debuff(uid = generateWheelItemUid(), modifier = -2)
        }
        override val id = Id.MongoliaDoesNotExist
        override val name = "Монголии не существует"
        override val description = """
            Монголия, кажется, и правда не существует. -${modifier.absoluteValue} к броску кубика на ход.
        """.trimIndent()
    }
}
