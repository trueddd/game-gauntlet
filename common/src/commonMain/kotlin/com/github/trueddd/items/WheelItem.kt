package com.github.trueddd.items

import com.benasher44.uuid.uuid4
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.Rollable
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Wheel item is an item that can be rolled on the wheel and somehow affect player state.
 * It can be an inventory item (that can be used later), an effect or a one-time event (that immediately affects state).
 * @property uid is an unique identifier for each item
 * @property id is an identifier of item - two items of the same type will have the same id
 */
@Serializable
sealed class WheelItem : Rollable {

    abstract val uid: String
    abstract val id: Id
    abstract override val name: String
    abstract override val description: String
    open val iconId: Int
        get() = id.value

    interface Factory {
        val itemId: Id
        fun create(): WheelItem
    }

    object Colors {
        const val EVENT = 0xFF5BC4DA
        const val PENDING_EVENT = 0xFF7554D0
        const val INVENTORY_ITEM = 0xFFE4B932
        const val BUFF = 0xFF2D765C
        const val DEBUFF = 0xFFCC476C
    }

    @JvmInline
    @Serializable
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
            val Relocation = Id(47)
            val ShoppingWithChat = Id(48)
            val TwoForThePriceOfOne = Id(49)
            val GreatEvent = Id(50)
            val AwfulEvent = Id(51)
            val UnrealBoost = Id(52)
            val MongoliaDoesNotExist = Id(53)
            val Teleport = Id(54)
            val NimbleFingers = Id(55)
            val Poll = Id(56)
            val Shitter = Id(57)
            val UnrecognizedDisk = Id(58)
            val FewLetters = Id(59)
            val ThereIsGiftAtYourDoor = Id(60)
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
    sealed class InventoryItem : WheelItem(), Usable {
        override val color: Long
            get() = Colors.INVENTORY_ITEM
    }

    @Serializable
    sealed class Event : WheelItem() {
        override val color: Long
            get() = Colors.EVENT
        abstract suspend fun invoke(globalState: GlobalState, rolledBy: Participant): GlobalState
    }

    @Serializable
    sealed class PendingEvent : WheelItem(), Usable {
        override val color: Long
            get() = Colors.PENDING_EVENT
    }

    @Serializable
    sealed class Effect : WheelItem() {

        @Serializable
        sealed class Buff : Effect() {
            override val color: Long
                get() = Colors.BUFF
        }

        @Serializable
        sealed class Debuff : Effect() {
            override val color: Long
                get() = Colors.DEBUFF
        }
    }
}

fun generateWheelItemUid(): String {
    return uuid4().toString()
}
