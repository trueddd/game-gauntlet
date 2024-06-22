package com.github.trueddd.items

import com.benasher44.uuid.uuid4
import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.PlayerName
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
        abstract suspend fun invoke(globalState: GlobalState, triggeredBy: PlayerName): GlobalState
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

    @Suppress("ConstPropertyName")
    companion object {
        const val PowerThrow = 1
        const val WeakThrow = 2
        const val YouDoNotNeedThis = 3
        const val SamuraiLunge = 4
        const val NegativeWheel = 5
        const val Reroll = 6
        const val Gamer = 7
        const val Viewer = 8
        const val ChargedDice = 9
        const val HaveATry = 10
        const val NoClownery = 11
        const val BananaSkin = 12
        const val WillOfChance = 13
        const val WillOfGoodChance = 14
        const val WillOfBadChance = 15
        const val BabySupport = 16
        const val LoyalModerator = 17
        const val ClimbingRope = 18
        const val WannaSwap = 19
        const val DontWannaPlayThis = 20
        const val HoleyPockets = 21
        const val PlusOneBuff = 22
        const val MinusOneDebuff = 23
        const val PlusToEveryone = 24
        const val PlusToEveryoneButYou = 25
        const val MinusToEveryone = 26
        const val MinusToEveryoneButYou = 27
        const val Plasticine = 28
        const val Earthquake = 29
        const val DontCare = 30
        const val RatMove = 31
        const val LuckyThrow = 32
        const val FamilyFriendlyStreamer = 33
        const val ForgotMyGame = 34
        const val NotDumb = 35
        const val YourStream = 36
        const val IWouldBeatIt = 37
        const val DontUnderstand = 38
        const val CompanySoul = 39
        const val EasterCakeBang = 40
        const val ImportSubstitution = 41
        const val Classic = 42
        const val FarmsDigsAndRobots = 43
        const val DiceBattle = 44
        const val Democracy = 45
        const val UnbelievableDemocracy = 46
        const val Relocation = 47
        const val ShoppingWithChat = 48
        const val TwoForThePriceOfOne = 49
        const val GreatEvent = 50
        const val AwfulEvent = 51
        const val UnrealBoost = 52
        const val MongoliaDoesNotExist = 53
        const val Teleport = 54
        const val NimbleFingers = 55
        const val Poll = 56
        const val Shitter = 57
        const val UnrecognizedDisk = 58
        const val FewLetters = 59
        const val ThereIsGiftAtYourDoor = 60
        const val Radio = 61
        const val LostFoot = 62
        const val LostLeg = 63
        const val ConcreteBoots = 64
        const val Sledgehammer = 65
        const val SledgehammerDebuff = 66
    }
}

fun generateWheelItemUid(): String {
    return uuid4().toString()
}
