package com.github.trueddd.items

import com.github.trueddd.utils.removeTabs
import com.trueddd.github.annotations.ItemFactory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("${WheelItem.ImportSubstitution}")
class ImportSubstitution private constructor(override val uid: String) : WheelItem.Effect.Debuff() {

    companion object {
        fun create() = ImportSubstitution(uid = generateWheelItemUid())
    }

    override val id = Id(ImportSubstitution)

    override val name = "Импортозамещение"

    override val description = """
        |Уделять игре «Веселая Ферма 4» по 30 минут каждый день с момента выпадения пункта до финала ивента 
        |или до момента сброса данного дебаффа.
    """.removeTabs()

    @ItemFactory
    class Factory : WheelItem.Factory {
        override val itemId = Id(ImportSubstitution)
        override fun create() = Companion.create()
    }
}
