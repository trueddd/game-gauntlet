package com.github.trueddd.data.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import kotlinx.serialization.Serializable

/**
 * Wheel item is an item that can be rolled on the wheel and somehow affect player state.
 * It can be an inventory item (that can be used later), an effect or a one-time event (that immediately affects state).
 * @property uid is an unique identifier for each item
 * @property id is an identifier of item - two items of the same type will have the same id
 */
@Serializable
sealed class WheelItem {

    abstract val uid: String
    abstract val id: Id
    abstract val name: String
    abstract val description: String

    interface Factory {
        val itemId: Id
        fun create(): WheelItem
    }

    @Serializable
    @JvmInline
    value class Id(val value: Int) {
        fun asString() = value.toString()
        companion object {
            val PowerThrow = Id(1)
            val WeakThrow = Id(2)
            val YouDoNotNeedThis = Id(3)
            val SamuraiLunge = Id(4)
            val NegativeWheel = Id(5)
            val Reroll = Id(6)
            val Gamer = Id(7)
            val Viewer = Id(8)
            val ChargedDice = Id(9)
            val HaveATry = Id(10)
            val NoClownery = Id(11)
            val BananaSkin = Id(12)
            val WillOfChance = Id(13)
            val WillOfGoodChance = Id(14)
            val WillOfBadChance = Id(15)
            val BabySupport = Id(16)
            val LoyalModerator = Id(17)
            val ClimbingRope = Id(18)
            val WannaSwap = Id(19)
            val DontWannaPlayThis = Id(20)
            val HoleyPockets = Id(21)
            val PlusOneBuff = Id(22)
            val MinusOneDebuff = Id(23)
            val PlusToEveryone = Id(24)
            val PlusToEveryoneButYou = Id(25)
            val MinusToEveryone = Id(26)
            val MinusToEveryoneButYou = Id(27)
            val Plasticine = Id(28)
            val Earthquake = Id(29)
            val DontCare = Id(30)
            val RatMove = Id(31)
            val LuckyThrow = Id(32)
            val FamilyFriendlyStreamer = Id(33)
            val ForgotMyGame = Id(34)
            val NotDumb = Id(35)
            val YourStream = Id(36)
            val IWouldBeatIt = Id(37)
            val DontUnderstand = Id(38)
            val CompanySoul = Id(39)
            val EasterCakeBang = Id(40)
            val ImportSubstitution = Id(41)
            val Classic = Id(42)
            val FarmsDigsAndRobots = Id(43)
            val DiceBattle = Id(44)
            val Democracy = Id(45)
            val UnbelievableDemocracy = Id(46)
            val Relocation = Id(46)
        }
    }

    override fun equals(other: Any?): Boolean {
        return id == (other as? WheelItem)?.id
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + id.value
        result = 31 * result + name.hashCode()
        return result
    }

    override fun toString(): String {
        return "(${id.value}-$uid/$name)"
    }

    @Serializable
    sealed class InventoryItem : WheelItem(), Usable

    @Serializable
    sealed class Event : WheelItem() {
        abstract suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState
    }

    @Serializable
    sealed class PendingEvent : WheelItem(), Usable

    @Serializable
    sealed class Effect : WheelItem() {

        @Serializable
        sealed class Buff : Effect()

        @Serializable
        sealed class Debuff : Effect()
    }
}
