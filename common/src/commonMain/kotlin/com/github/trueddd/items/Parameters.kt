package com.github.trueddd.items

import com.github.trueddd.data.GlobalState
import com.github.trueddd.data.Participant
import com.github.trueddd.data.PlayerName
import com.github.trueddd.data.PlayerState

sealed interface Parameters {
    data class One<T1>(
        val parameter1: T1
    ) : Parameters
    data class Two<T1, T2>(
        val parameter1: T1,
        val parameter2: T2
    ) : Parameters
    data class Three<T1, T2, T3>(
        val parameter1: T1,
        val parameter2: T2,
        val parameter3: T3
    ) : Parameters
}

interface Parametrized<P : Parameters> {
    val parametersScheme: List<ParameterType>
    fun getParameters(rawArguments: List<String>, currentState: GlobalState): P
}

sealed class ParameterType {

    abstract val name: String
    open val description: String?
        get() = null

    class Bool(override val name: String) : ParameterType()
    class Int(override val name: String) : ParameterType()
    class Player(
        override val name: String,
        val predicate: (PlayerName, PlayerState) -> Boolean = { _, _ -> true },
        override val description: String? = null
    ) : ParameterType()
    class MyItem(
        override val name: String,
        val predicate: ((WheelItem) -> Boolean) = { true },
    ) : ParameterType()
    class ForeignItem(
        override val name: String,
        val predicate: ((WheelItem) -> Boolean) = { true }
    ) : ParameterType()
    class Item(
        override val name: String,
        val predicate: ((WheelItem) -> Boolean) = { true }
    ) : ParameterType()
    class Genre(override val name: String) : ParameterType()
}

@Throws(IllegalArgumentException::class)
fun List<String>.getBooleanParameter(index: Int = 0, optional: Boolean = false): Boolean? {
    return when (val value = this.getOrNull(index)) {
        "1" -> true
        "0" -> false
        else -> if (optional) {
            null
        } else {
            throw IllegalArgumentException("Boolean argument must be passed, but was $value")
        }
    }
}

@Throws(IllegalArgumentException::class)
fun List<String>.getStringParameter(index: Int = 0): String {
    return this.getOrNull(index)
        ?: throw IllegalArgumentException("String argument must be passed, but was ${getOrNull(index)}")
}

@Throws(IllegalArgumentException::class)
fun List<String>.getIntParameter(index: Int = 0): Int {
    val value = this.getOrNull(index)
        ?: throw IllegalArgumentException("Number must be specified as parameter with index $index")
    return value.toIntOrNull()
        ?: throw IllegalArgumentException("Cannot parse $value as number")
}

@Throws(IllegalArgumentException::class)
fun List<String>.getParticipantParameter(
    index: Int = 0,
    globalState: GlobalState,
    optional: Boolean = false
): Participant? {
    val name = this.getOrNull(index)
    return when {
        name != null -> globalState.participantByName(name)
        optional -> null
        else -> throw IllegalArgumentException("Player name must be specified as parameter with index $index")
    }
}
