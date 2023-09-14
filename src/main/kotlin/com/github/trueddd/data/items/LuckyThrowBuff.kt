package com.github.trueddd.data.items

import com.github.trueddd.data.Game
import com.github.trueddd.utils.generateWheelItemUid
import kotlinx.serialization.Serializable

@Serializable
class LuckyThrowBuff private constructor(
    override val uid: String,
    val genre: Game.Genre
) : WheelItem.Effect.Buff() {

    companion object {
        fun create(genre: Game.Genre) = LuckyThrowBuff(uid = generateWheelItemUid(), genre)
    }

    override val id = Id.LuckyThrowBuff

    override val name = "Счастливый бросок. ${genre.name}"

    override val description = """
        Если после следующего броска кубика стример попадает на клетку с жанром ${genre.name}, 
        то эта клетка автоматически засчитывается пройденной и стример двигается дальше.
    """.trimIndent()
}
