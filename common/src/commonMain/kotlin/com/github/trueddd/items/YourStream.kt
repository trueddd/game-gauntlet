package com.github.trueddd.items

import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.YourStream}")
class YourStream private constructor(override val uid: String) : WheelItem.Effect.Buff() {

    companion object {
        fun create() = YourStream(uid = generateWheelItemUid())
    }

    override val id = Id(YourStream)

    override val name = "\"Это твой стрим, делай, что хочешь\""

    override val description = """
        При следующем ролле игры участник, наролливший этот пункт, может сам выбрать в какую игру ему играть из тех, 
        что видны на экране колеса. Гуглить информацию об игре перед выбором разрешается, все таки стрим твой.
    """.trimIndent()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(YourStream)
        override fun create() = Companion.create()
    }
}
