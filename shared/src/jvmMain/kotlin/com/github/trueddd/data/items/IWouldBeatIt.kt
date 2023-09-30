package com.github.trueddd.data.items

import com.github.trueddd.utils.generateWheelItemUid
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.Serializable

@Serializable
class IWouldBeatIt private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        fun create() = IWouldBeatIt(uid = generateWheelItemUid())
    }

    override val id = Id.IWouldBeatIt

    override val name = "А вот я бы прошел"

    override val description = """
        Следующая игра проходится из списка дропнутых на текущем ивенте игр всеми участниками.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id.IWouldBeatIt
        override fun create() = IWouldBeatIt.create()
    }
}
