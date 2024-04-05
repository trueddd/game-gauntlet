package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.without
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.FamilyFriendlyStreamer}")
class FamilyFriendlyStreamer private constructor(override val uid: String) : WheelItem.PendingEvent() {

    companion object {
        fun create() = FamilyFriendlyStreamer(uid = generateWheelItemUid())
    }

    override val id = Id(FamilyFriendlyStreamer)

    override val name = "Стример для всей семьи"

    override val description = """
        На протяжении всей игры запрещено материться (без привязки к языку) и шутить похабные шутки. 
        Слово "сука" хоть и литературное, но запрещается к использованию. Помните, что Вас смотрят дети. 
        При провале данного пункта получи -3 к броску кубика. В случае выполнения - уважение от зрителей с детьми.
    """.trimIndent()

    override suspend fun use(usedBy: Participant, globalState: GlobalState, arguments: List<String>): GlobalState {
        return globalState.updatePlayer(usedBy) { playerState ->
            playerState.copy(
                pendingEvents = playerState.pendingEvents.without(uid),
                effects = playerState.effects + Debuff.create(),
            )
        }
    }

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(FamilyFriendlyStreamer)
        override fun create() = Companion.create()
    }

    @Serializable
    class Debuff private constructor(
        override val uid: String,
        override val modifier: Int = -3
    ) : Effect.Debuff(), DiceRollModifier {

        companion object {
            fun create() = Debuff(uid = generateWheelItemUid())
        }

        override val id = Id(FamilyFriendlyStreamer)

        override val name = "Стример не для всей семьи"

        override val description = "-3 к броску кубика на ход. А не надо было сквернословить..."
    }
}
