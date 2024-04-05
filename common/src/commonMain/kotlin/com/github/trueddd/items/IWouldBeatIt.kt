package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.IWouldBeatIt}")
class IWouldBeatIt private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        fun create() = IWouldBeatIt(uid = generateWheelItemUid())
    }

    override val id = Id(IWouldBeatIt)

    override val name = "А вот я бы прошел"

    override val description = """
        Следующая игра проходится из списка дропнутых на текущем ивенте игр всеми участниками.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(IWouldBeatIt)
        override fun create() = Companion.create()
    }
}
